package eu.buildserscoffee.web.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;

@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    private static final String URL = "oauth2/authorization/google";

    public LoginView() {
        setPadding(true);
        setAlignItems(Alignment.CENTER);
    }

    @PostConstruct
    public void initView() {
        Anchor gplusLoginButton = new Anchor(URL, "Se connecter avec Google");
        gplusLoginButton.getStyle().set("margin-top", "100px");
        add(gplusLoginButton);
    }
}
