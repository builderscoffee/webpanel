package eu.buildserscoffee.web.services.playpen.listeners;

import eu.buildserscoffee.web.services.playpen.PlaypenListener;
import io.playpen.core.networking.TransactionInfo;
import io.playpen.core.protocol.Commands;

@FunctionalInterface
public interface ProvisionResponseListener extends PlaypenListener {

    @Override
    default void receivedListResponse(Commands.C_CoordinatorListResponse response, TransactionInfo info){

    }

    @Override
    default void receivedConsoleAttach(String consoleId, TransactionInfo info){

    }

    @Override
    default void receivedConsoleAttachFail(TransactionInfo info){

    }

    @Override
    default void receivedDetachConsole(String consoleId, TransactionInfo info){

    }

    @Override
    default void receivedConsoleMessage(String consoleId, String value, TransactionInfo info){

    }

    @Override
    default void receivedPackageList(Commands.C_PackageList list, TransactionInfo info){

    }

    @Override
    void receivedProvisionResponse(Commands.C_ProvisionResponse response, TransactionInfo info);

    @Override
    default void receivedAccessDenied(Commands.C_AccessDenied message, TransactionInfo info){

    }

    @Override
    default void receivedAck(Commands.C_Ack c_ack){

    }

    @Override
    default void receivedPackageResponse(Commands.PackageResponse response, TransactionInfo info){

    }
}
