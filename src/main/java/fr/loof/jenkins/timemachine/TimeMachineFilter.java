package fr.loof.jenkins.timemachine;

import org.eclipse.jgit.api.errors.GitAPIException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        timeMachine.start();
        filterChain.doFilter(request, response);
        try {
            timeMachine.commit();
        } catch (GitAPIException e) {
            throw new RuntimeException("oups", e);
        }

    }

    @Override
    public void destroy() {
        // noop
    }
}
