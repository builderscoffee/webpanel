package eu.buildserscoffee.web.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import eu.buildserscoffee.web.data.UserSession;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@CssImport("./styles/main.css")
public class AdminTemplate extends Div {

    @Autowired
    UserSession userSession;

    private final Div content = new Div();
    private final NavBar navBar = new NavBar();
    private final SideBar sideBar = new SideBar();

    public AdminTemplate() {
        addClassNames("container");

        content.addClassNames("main-content");

        add(navBar, sideBar, content);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        navBar.getNameSpan().setText(userSession.getUser().getFirstName());
        navBar.getProfileImage().setSrc(userSession.getUser().getPicture());
        navBar.getProfileImage().setAlt("User Image");
    }
}
