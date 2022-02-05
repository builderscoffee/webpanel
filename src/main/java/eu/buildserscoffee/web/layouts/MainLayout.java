package eu.buildserscoffee.web.layouts;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLink;
import eu.buildserscoffee.web.views.DashboardView;
import eu.buildserscoffee.web.views.PlaypenView;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Objects;

@Getter
public class MainLayout extends AppLayout {

    @Getter(AccessLevel.PRIVATE)
    final VerticalLayout viewHeader;
    @Getter(AccessLevel.PRIVATE)
    final Tabs pageTabs;
    final H2 pageTitle;

    public MainLayout() {
        H1 appTitle = new H1("BuildersCoffee Panel");
        appTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("line-height", "var(--lumo-size-l)")
                .set("margin", "0 var(--lumo-space-m)");

        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(createTab(VaadinIcon.DASHBOARD, "Dashboard", DashboardView.class));
        tabs.add(createTab(VaadinIcon.SERVER, "Playpen", PlaypenView.class));

        DrawerToggle toggle = new DrawerToggle();

        // Page title
        pageTitle = new H2("Orders");
        pageTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        pageTabs = new Tabs();

        HorizontalLayout wrapper = new HorizontalLayout(toggle, pageTitle);
        wrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        wrapper.setSpacing(false);

        viewHeader = new VerticalLayout(wrapper);
        viewHeader.setPadding(false);
        viewHeader.setSpacing(false);

        addToDrawer(appTitle, tabs);
        addToNavbar(viewHeader);

        setPrimarySection(Section.DRAWER);
    }

    /**
     * Returns the current active instance
     *
     * @return
     */
    public static MainLayout getInstance() {
        return (MainLayout) UI.getCurrent().getChildren()
                .filter(component -> component.getClass() == MainLayout.class).findFirst().orElse(null);
    }

    public Tab createTab(VaadinIcon icon, String text, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        final RouterLink routerlink = new RouterLink(null, navigationTarget);
        final Span span = new Span(text);
        span.getStyle().set("padding-left", "10px");
        routerlink.add(new Icon(icon));
        routerlink.add(span);
        return new Tab(routerlink);
    }

    public void updatePageTabs(Tab... tabs){
        if(Objects.isNull(tabs)){
            if(Objects.nonNull(pageTabs))
                viewHeader.remove(pageTabs);
        }
        else{
            viewHeader.add(pageTabs);
            pageTabs.removeAll();
            pageTabs.add(tabs);
        }
    }
}