package com.gitlab.jeeto.oboco;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        
        PrintWriter printWriter = response.getWriter();
        printWriter.println("<html>");
        printWriter.println("<head>");
        printWriter.println("</head>");
        printWriter.println("<h1>OBOCO</h1>");
        printWriter.println("<ul>");
        printWriter.println("<li><a href='/api/openapi.json'>API</a></li>");
        printWriter.println("<li><a href='/api-web'>API Web</a></li>");
        printWriter.println("<li><a href='/opds/v1.2'>OPDS v1.2</a></li>");
        printWriter.println("<li><a href='/web'>Web</a></li>");
        printWriter.println("</ul>");
        printWriter.println("</body>");
        printWriter.println("</html>");
    }
}
