package fr.loof.jenkins.timemachine;

import org.eclipse.jgit.api.errors.GitAPIException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class TimeMachineFilter implements Filter {


    private TimeMachine timeMachine;

    public TimeMachineFilter(TimeMachine timeMachine) {
        this.timeMachine = timeMachine;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest h = (HttpServletRequest) servletRequest;
        timeMachine.start();
        filterChain.doFilter(servletRequest, servletResponse);
        try {
            timeMachine.commit(guessAction(h));
        } catch (GitAPIException e) {
            throw new RuntimeException("oups", e);
        }

    }

    /**
     * Guess user intent based on the requested URI
     */
    private String guessAction(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        switch (pathInfo) {
            case "/configSubmit"               : return "configure system";
            case "/setupWizard/createAdminUser": return "create Administrator from setup wizard";
            case "/configureSecurity/configure": return "configure global security";
            case "/configureTools/configure"   : return "configure tools";
            // ...
        }

        if (pathInfo.endsWith("/createItem")) {
            return "create new Item";  // TODO find way to capture item type & item name
        }

        if (pathInfo.startsWith("/job/") && pathInfo.endsWith("/configSubmit")) {
            return "edited configuration of job " + pathInfo.substring(5, pathInfo.length() - 13);
        }

        return pathInfo;
    }

    @Override
    public void destroy() {

    }
}
