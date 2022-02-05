package eu.buildserscoffee.web.components;

import com.vaadin.flow.component.html.*;
import lombok.Getter;

@Getter
public class NavBar extends Nav {

    private final Span appTitle = new Span("BuildersCoffee Panel");
    private final Div rightDiv = new Div();
    private final Span nameSpan = new Span();
    private final Image profileImage = new Image();
    private final Anchor logoutAnchor = new Anchor("/logout", "Logout");

    public NavBar() {
        addClassNames("navbar");
        appTitle.addClassNames("app-title");
        rightDiv.addClassNames("navbar-right");
        logoutAnchor.getElement().setAttribute("router-ignore", true);

        rightDiv.add(profileImage, nameSpan, logoutAnchor);
        add(appTitle, rightDiv);
    }
}
