package com.cloudbees.jenkins.plugins.timemachine;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
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
        timeMachine.start(guessAction(h.getPathInfo()));
        filterChain.doFilter(servletRequest, servletResponse);
        try {
            timeMachine.commit();
        } catch (GitAPIException e) {
            throw new RuntimeException("oups", e);
        }

    }

    /**
     * Guess user intent based on the requested URI
     */
    private String guessAction(String pathInfo) {
        return pathInfo;
    }

    @Override
    public void destroy() {

    }
}
