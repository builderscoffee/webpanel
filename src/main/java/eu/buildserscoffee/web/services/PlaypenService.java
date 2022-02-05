package eu.buildserscoffee.web.services;

import com.vaadin.flow.server.*;
import eu.buildserscoffee.web.properties.PlaypenProperties;
import eu.buildserscoffee.web.services.playpen.PlaypenClient;
import eu.buildserscoffee.web.services.playpen.PlaypenLinkedListener;
import eu.buildserscoffee.web.services.playpen.PlaypenListener;
import eu.buildserscoffee.web.services.playpen.listeners.ConsoleMessageListener;
import eu.buildserscoffee.web.utils.FirstInFirstOutList;
import eu.buildserscoffee.web.utils.Quadruple;
import io.playpen.core.networking.TransactionInfo;
import io.playpen.core.p3.P3Package;
import io.playpen.core.protocol.Commands;
import io.playpen.core.protocol.Coordinator;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Service
@Configuration
@EnableConfigurationProperties(PlaypenProperties.class)
public class PlaypenService implements VaadinServiceInitListener {


    @Value("${playpen.connection.name}")
    String name;

    @Value("${playpen.connection.secret-key}")
    String secretKey;

    @Value("${playpen.connection.uuid}")
    String uuid;

    @Value("${playpen.connection.ip}")
    String ip;

    @Value("${playpen.connection.port}")
    String port;

    PlaypenClient client;

    @Getter
    private List<PlaypenLinkedListener> listeners = new ArrayList<>();

    @Getter
    private List<Quadruple<String, String, String, FirstInFirstOutList<String>>> attachedServers = new ArrayList<>();
    private List<Triple<String, String, TransactionInfo>> attachedServersQueue = new ArrayList();

    @PostConstruct
    public void init() {

        final InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            log.fatal("Unable to convert the String \"" + ip + "\" to an InetAddress");
            e.printStackTrace();
            return;
        }
        int portNumber;
        try {
            portNumber = Integer.parseUnsignedInt(port);
        } catch (NumberFormatException e) {
            log.fatal("Unable to convert the String \"" + port + "\" to an Integer");
            e.printStackTrace();
            return;
        }
        client = new PlaypenClient(name, uuid, secretKey, inetAddress, portNumber);

        client.getListeners().add(new PlaypenListener() {
            @Override
            public void receivedListResponse(Commands.C_CoordinatorListResponse response, TransactionInfo info) {
                List<Quadruple<String, String, String, FirstInFirstOutList<String>>> toRemove = new ArrayList<>();
                response.getCoordinatorsList().forEach(lc -> {
                    lc.getServersList().forEach(server -> {
                        if (!attachedServers.stream().anyMatch(triple -> triple.getOne().equals(lc.getUuid())
                                && triple.getTwo().equals(server.getUuid()))) {
                            attachedServersQueue.add(Triple.of(lc.getUuid(), server.getUuid(), sendAttachConsole(lc.getUuid(), server.getUuid())));
                        }
                    });
                });

                attachedServers.stream()
                        .filter(triple -> !response.getCoordinatorsList().stream()
                                .map(Coordinator.LocalCoordinator::getUuid)
                                .anyMatch(lcId -> lcId.equals(triple.getOne()))
                                && !response.getCoordinatorsList().stream()
                                .filter(lc -> lc.getUuid().equals(triple.getOne()))
                                .findFirst().get()
                                .getServersList().stream()
                                .anyMatch(server -> server.getUuid().equals(triple.getTwo())))
                        .forEach(toRemove::add);

                toRemove.forEach(attachedServers::remove);

                getListeners().forEach(pll -> pll.getListener().receivedListResponse(response, info));
            }

            @Override
            public void receivedConsoleAttach(String consoleId, TransactionInfo info) {
                Triple<String, String, TransactionInfo> triple = attachedServersQueue.stream()
                        .filter(t -> t.getRight().equals(info))
                        .findFirst().orElse(null);
                if (Objects.nonNull(triple)) {
                    attachedServers.add(Quadruple.of(triple.getLeft(), triple.getMiddle(), consoleId, new FirstInFirstOutList(200)));
                }
                //getListeners().forEach(pll -> pll.getListener().receivedConsoleAttach(consoleId, info));
            }

            @Override
            public void receivedConsoleAttachFail(TransactionInfo info) {
                getListeners().forEach(pll -> pll.getListener().receivedConsoleAttachFail(info));
            }

            @Override
            public void receivedDetachConsole(String consoleId, TransactionInfo info) {
                List<Quadruple<String, String, String, FirstInFirstOutList<String>>> toRemove = new ArrayList<>();

                attachedServers.stream()
                        .filter(triple -> triple.getThree().equals(consoleId))
                        .forEach(toRemove::add);

                toRemove.forEach(attachedServers::remove);
                getListeners().forEach(pll -> pll.getListener().receivedDetachConsole(consoleId, info));
            }

            @Override
            public void receivedConsoleMessage(String consoleId, String value, TransactionInfo info) {
                attachedServers.stream()
                        .filter(triple -> triple.getThree().equals(consoleId))
                        .forEach(triple -> triple.getFour().add(value));
                getListeners().forEach(pll -> pll.getListener().receivedConsoleMessage(consoleId, value, info));
            }

            @Override
            public void receivedPackageList(Commands.C_PackageList list, TransactionInfo info) {
                getListeners().forEach(pll -> pll.getListener().receivedPackageList(list, info));
            }

            @Override
            public void receivedProvisionResponse(Commands.C_ProvisionResponse response, TransactionInfo info) {
                getListeners().forEach(pll -> pll.getListener().receivedProvisionResponse(response, info));
            }

            @Override
            public void receivedAccessDenied(Commands.C_AccessDenied message, TransactionInfo info) {
                getListeners().forEach(pll -> pll.getListener().receivedAccessDenied(message, info));
            }

            @Override
            public void receivedAck(Commands.C_Ack c_ack) {
                getListeners().forEach(pll -> pll.getListener().receivedAck(c_ack));
            }

            @Override
            public void receivedPackageResponse(Commands.PackageResponse response, TransactionInfo info) {
                getListeners().forEach(pll -> pll.getListener().receivedPackageResponse(response, info));
            }
        });
    }

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        VaadinService.getCurrent().addSessionDestroyListener(e -> removeListeners(e.getSession().getSession()));
    }

    @Scheduled(fixedRate = 5000)
    public void refreshServerList() {
        sendListRequest();
    }

    public boolean exist(String consoleId) {
        return attachedServers.stream().anyMatch(triple -> triple.getThree().equals(consoleId));
    }

    public void addListener(PlaypenListener listener) {
        final PlaypenLinkedListener pll = getListeners().stream()
                .filter(pll2 -> pll2.getSessionId().equals(VaadinSession.getCurrent().getSession().getId())
                        && pll2.getListener().getClass().equals(listener.getClass()))
                .findFirst().orElse(null);
        if(listener instanceof ConsoleMessageListener){
            attachedServers.forEach(triple -> triple.getFour().forEach(value -> listener.receivedConsoleMessage(triple.getThree(), value, null)));
        }
        if (Objects.isNull(pll))
            getListeners().add(new PlaypenLinkedListener(VaadinSession.getCurrent().getSession().getId(), listener));
        else
            pll.setListener(listener);
    }

    public void removeListener(PlaypenListener listener) {
        List<PlaypenLinkedListener> toRemove = new ArrayList<>();
        getListeners().stream()
                .filter(pll -> pll.getSessionId().equals(VaadinSession.getCurrent().getSession().getId())
                        && pll.getListener().getClass().equals(listener.getClass()))
                .forEach(toRemove::add);
        toRemove.forEach(client.getListeners()::remove);
    }

    public void removeListeners() {
        removeListeners(VaadinSession.getCurrent().getSession());
    }

    public void removeListeners(WrappedSession session) {
        List<PlaypenLinkedListener> toRemove = new ArrayList<>();
        getListeners().stream()
                .filter(pll -> pll.getSessionId().equals(session.getId()))
                .forEach(toRemove::add);
        toRemove.forEach(client.getListeners()::remove);
    }

    public void sendCommand(String consoleId, String coordinatorId, String serverId, String value){
        sendInput(coordinatorId, serverId, value.trim() + "\n");
        client.getListeners().forEach(listener -> listener.receivedConsoleMessage(consoleId, ">> " + value.trim(), null));
    }

    public boolean isConnected() {
        //System.out.println("Check playpen connection (connected:" + client.isConnected() + ")");
        return client.isConnected() ? true : client.start();
    }

    private void checkConnection() {
        if (!isConnected())
            log.warn("");
    }

    public boolean sendSync() {
        checkConnection();
        return client.sendSync();
    }

    public TransactionInfo sendListRequest() {
        checkConnection();
        return client.sendListRequest();
    }

    public TransactionInfo sendRequestPackageList() {
        checkConnection();
        return client.sendRequestPackageList();
    }

    public boolean sendShutdown(String coordId) {
        checkConnection();
        return client.sendShutdown(coordId);
    }

    public TransactionInfo sendAttachConsole(String coordId, String serverId) {
        checkConnection();
        return client.sendAttachConsole(coordId, serverId);
    }

    public void sendDetachConsole(String consoleId) {
        checkConnection();
        client.sendDetachConsole(consoleId);
    }

    public boolean sendInput(String coordId, String serverId, String input) {
        checkConnection();
        return client.sendInput(coordId, serverId, input);
    }

    public boolean sendDeprovision(String coordId, String serverId, boolean force) {
        checkConnection();
        return client.sendDeprovision(coordId, serverId, force);
    }

    public boolean sendFreezeServer(String coordId, String serverId) {
        checkConnection();
        return client.sendFreezeServer(coordId, serverId);
    }

    public TransactionInfo sendProvision(String id, String version, String coordinator, String serverName, Map<String, String> properties) {
        checkConnection();
        return client.sendProvision(id, version, coordinator, serverName, properties);
    }

    public boolean sendPromote(String id, String version) {
        checkConnection();
        return client.sendPromote(id, version);
    }

    public boolean sendPackage(P3Package p3) {
        checkConnection();
        return client.sendPackage(p3);
    }

    public TransactionInfo sendPackageRequest(String id, String version) {
        checkConnection();
        return client.sendPackageRequest(id, version);
    }

    public void setAwaitingAckResponse(boolean awaitingAckResponse) {
        checkConnection();
        client.setAwaitingAckResponse(awaitingAckResponse);
    }
}
