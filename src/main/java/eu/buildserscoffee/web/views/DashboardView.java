package eu.buildserscoffee.web.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import eu.buildserscoffee.web.components.AdminTemplate;
import eu.buildserscoffee.web.data.UserSession;
import eu.buildserscoffee.web.layouts.MainLayout;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "")
public class DashboardView extends AdminTemplate {

    @Autowired
    UserSession userSession;

    VerticalLayout viewHeader;
    Tabs pageTabs;
    H2 pageTitle;
    @PostConstruct
    public void init() {

        /*Div div = new Div();
        div.setText("Hello " + userSession.getUser().getFirstName() + " " + userSession.getUser().getLastName());
        div.getElement().getStyle().set("font-size", "xx-large");

        Image image = new Image(userSession.getUser().getPicture(), "User Image");

        // Spring maps the 'logout' url so we should ignore it
        Anchor logout = new Anchor("/logout", "Logout");
        logout.getElement().setAttribute("router-ignore", true);

        //setAlignItems(Alignment.CENTER);
        getContent().add(div, image, logout);*/
    }


    public Tab createTab(VaadinIcon icon, String text, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        final RouterLink routerlink = new RouterLink(null, navigationTarget);
        final Span span = new Span(text);
        span.getStyle().set("padding-left", "10px");
        routerlink.add(new Icon(icon));
        routerlink.add(span);
        return new Tab(routerlink);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        //final MainLayout layout = MainLayout.getInstance();
        //layout.getPageTitle().setText("Dashboard");
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }
}
