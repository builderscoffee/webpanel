package eu.buildserscoffee.web.views;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import eu.buildserscoffee.web.components.AdminTemplate;
import eu.buildserscoffee.web.services.PlaypenService;
import eu.buildserscoffee.web.services.playpen.listeners.ConsoleDetachListener;
import eu.buildserscoffee.web.services.playpen.listeners.ConsoleMessageListener;
import eu.buildserscoffee.web.services.playpen.listeners.ListResponseListener;
import eu.buildserscoffee.web.utils.Quadruple;
import io.playpen.core.protocol.Coordinator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.Objects;

@Route(value = "playpen")
@Push
@CssImport("./styles/playpen.css")
@SessionScope
public class PlaypenView extends AdminTemplate implements BeforeEnterObserver, BeforeLeaveObserver {


    private final Div subSidebar = new Div();
    private final Span subSidebarTitle = new Span();
    private final Div subSidebarContainer = new Div();

    private final Div subNavbar = new Div();
    private Span subNavbarTitle;
    private Span subNavbarStopServer;

    private final Div subContent = new Div();
    private Div subContentConsole;
    private TextField subContentConsoleField;


    @Autowired
    PlaypenService playpenService;

    String attachedServerName = null;
    String attachedConsoleId = null;

    @PostConstruct
    public void init() {
        subNavbar.addClassNames("playpen-navbar");
        subSidebar.addClassNames("playpen-sidebar");
        subContent.addClassNames("playpen-content");

        subSidebarTitle.addClassNames("playpen-sidebar-title");
        subSidebarTitle.setText("Servers");
        subSidebarContainer.addClassNames("sidebar-container", "playpen-container");

        subSidebar.add(subSidebarTitle, subSidebarContainer);
        getContent().add(subNavbar, subSidebar, subContent);


    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        loadServersList(beforeEnterEvent.getUI());
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        playpenService.removeListeners();
    }

    public void loadServersList(UI ui) {
        final ListResponseListener listResponseListener = (response, info) -> {
            if (Objects.isNull(ui))
                return;

            ui.access(() -> {
                subSidebarContainer.removeAll();
                response.getCoordinatorsList().stream()
                        .forEach(lc -> lc.getServersList().stream()
                                .sorted(Comparator.comparing(Coordinator.Server::getName))
                                .forEach(server -> {
                            final Anchor anchor = new Anchor();
                            anchor.setText(server.getName());
                            anchor.getElement().addEventListener("click", e -> loadConsole(ui, server.getName(), lc.getUuid(), server.getUuid()));
                            subSidebarContainer.add(anchor);
                        }));
            });
        };

        playpenService.addListener(listResponseListener);
        playpenService.sendListRequest();
    }

    public void loadConsole(UI ui, String name, String coordinatorId, String serverId) {
        final ConsoleMessageListener consoleMessageListener = (consoleId1, value, info) -> {
            if (Objects.isNull(ui) || !consoleId1.equals(attachedConsoleId))
                return;

            ui.access(() -> {
                final Span span = new Span(value);
                subContentConsole.add(span);
            });
        };


        attachedServerName = name;
        attachedConsoleId = playpenService.getAttachedServers().stream()
                .filter(triple -> triple.getOne().equals(coordinatorId)
                        && triple.getTwo().equals(serverId))
                .map(Quadruple::getThree)
                .findFirst().orElse(null);

        ui.access(() -> {
            subNavbar.removeAll();
            subNavbarTitle = new Span();
            subNavbarTitle.addClassNames("playpen-navbar-title");

            subNavbarStopServer = new Span();
            subNavbarStopServer.addClassNames("playpen-navbar-serverstop");

            subNavbarStopServer.addClickListener(e -> {
                playpenService.sendDeprovision(coordinatorId, serverId, false);
            });

            subNavbar.add(subNavbarTitle, subNavbarStopServer);


            subContent.addClassNames("active");

            subNavbarTitle.setText(name);
            subContent.removeAll();

            subContentConsole = new Div();
            subContentConsole.addClassNames("playpen-content-console");

            subContentConsoleField = new TextField();
            subContentConsoleField.addClassNames("playpen-content-field");
            final ComponentEventListener componentEventListener = e -> {
                ui.access(() -> {
                    if(subContentConsoleField.getValue().trim().length() > 0){
                        playpenService.sendCommand(attachedConsoleId, coordinatorId, serverId, subContentConsoleField.getValue());
                        subContentConsoleField.setValue("");
                    }
                });
            };

            subContentConsoleField.addKeyDownListener(Key.ENTER, componentEventListener);
            subContentConsoleField.addKeyDownListener(Key.NUMPAD_ENTER, componentEventListener);

            subContent.add(subContentConsole, subContentConsoleField);
        });

        final ConsoleDetachListener consoleDetachListener = (consoleId, info) -> {
            if(Objects.isNull(ui) || !attachedConsoleId.equals(consoleId))
                return;

            ui.access(() -> {
                subContent.removeClassNames("active");
                subNavbar.removeAll();
                subContent.removeAll();
            });
        };

        playpenService.addListener(consoleMessageListener);
        playpenService.addListener(consoleDetachListener);
    }
}
