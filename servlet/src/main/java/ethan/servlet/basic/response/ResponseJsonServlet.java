package ethan.servlet.basic.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import ethan.servlet.basic.HelloData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {
    private ObjectMapper objectMapper= new ObjectMapper();
    // ContentType : application/json
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        HelloData helloData = new HelloData();
        helloData.setUsername("ethan");
        helloData.setAge(30);
        // {"username": "ethan", "age" : 30}
        String result = objectMapper.writeValueAsString(helloData);
        response.getWriter().write(result);
    }
}
