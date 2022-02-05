package eu.buildserscoffee.web.services.playpen;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaypenLinkedListener {

    private String sessionId;
    private PlaypenListener listener;

    public PlaypenLinkedListener(String sessionId, PlaypenListener listener) {
        this.sessionId = sessionId;
        this.listener = listener;
    }
}
