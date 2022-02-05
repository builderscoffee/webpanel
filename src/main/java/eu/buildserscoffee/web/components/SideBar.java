package eu.buildserscoffee.web.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.RouterLink;
import eu.buildserscoffee.web.views.DashboardView;
import eu.buildserscoffee.web.views.PlaypenView;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
public class SideBar extends Div {

    private final UnorderedList container = new UnorderedList();
    private final ListItem item1 = new ListItem();
    private final RouterLink routerLink1 = new RouterLink();
    private final ListItem item2 = new ListItem();
    private final RouterLink routerLink2 = new RouterLink();

    public SideBar() {
        addClassNames("main-sidebar");

        container.addClassNames("sidebar-container");

        routerLink1.setRoute(DashboardView.class);
        routerLink1.add(new Icon(VaadinIcon.DASHBOARD));
        routerLink1.add(new Span("Dashboard"));
        item1.add(routerLink1);

        routerLink2.setRoute(PlaypenView.class);
        routerLink2.add(new Icon(VaadinIcon.SERVER));
        routerLink2.add(new Span("Playpen"));
        item2.add(routerLink2);

        container.add(item1, item2);

        add(container);
    }
}
