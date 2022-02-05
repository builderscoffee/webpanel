package eu.buildserscoffee.web;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class Test implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addUIInitListener(uiEvent -> uiEvent.getUI().addBeforeEnterListener(this::check));
        //serviceInitEvent.getSource().addUIInitListener(uiEvent -> uiEvent.getUI().addAttachListener());
    }

    private void check(BeforeEnterEvent event){
        //System.out.println(event.getLocation().getPath());
    }

    //private void check()
}
