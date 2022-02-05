package eu.buildserscoffee.web.services.playpen;

import io.playpen.core.networking.TransactionInfo;
import io.playpen.core.protocol.Commands;

public interface PlaypenListener {
    void receivedListResponse(Commands.C_CoordinatorListResponse response, TransactionInfo info);
    void receivedConsoleAttach(String consoleId, TransactionInfo info);
    void receivedConsoleAttachFail(TransactionInfo info);
    void receivedDetachConsole(String consoleId, TransactionInfo info);
    void receivedConsoleMessage(String consoleId, String value, TransactionInfo info);
    void receivedPackageList(Commands.C_PackageList list, TransactionInfo info);
    void receivedProvisionResponse(Commands.C_ProvisionResponse response, TransactionInfo info);
    void receivedAccessDenied(Commands.C_AccessDenied message, TransactionInfo info);
    void receivedAck(Commands.C_Ack c_ack);
    void receivedPackageResponse(Commands.PackageResponse response, TransactionInfo info);
}
