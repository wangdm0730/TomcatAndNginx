package server;

import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class RequestProcessor extends Thread {

    private Socket socket;
//    private Map<String,HttpServlet> servletMap;
//    public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap) {
//        this.socket = socket;
//        this.servletMap = servletMap;
//    }
    private LagouMapper mapper;
    public RequestProcessor(Socket socket, LagouMapper mapper) {
    this.socket = socket;
    this.mapper = mapper;
}

    @Override
    public void run() {
        try{
            InputStream inputStream = socket.getInputStream();
            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());
            Servlet servlet = getServlet(request.getUrl());
            // 静态资源处理
            if(servlet == null) {
                response.outputHtml(request.getUrl());
            }else{
                // 动态资源servlet请求
                servlet.service(request,response);
            }
            socket.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 解析mapper中wrapper
     * @return
     */
    private Servlet getServlet(String url) {
        String[] split = url.split("/");
        String contextFlag = split[1];

        List<LagouContext> contexts = mapper.getLagouHosts().get(0).getLagouContexts();
        for(LagouContext context: contexts) {
            if(contextFlag.equalsIgnoreCase(context.getPath())) {
                List<LagouWrapper> wrappers = context.getLagouWrappers();
                for(LagouWrapper wrapper:wrappers) {
                    if(wrapper.getUrlPattern().equalsIgnoreCase(url.substring(("/" + contextFlag).length()))) {
                        return wrapper.getServlet();
                    }
                }
            }
        }
        return null;
    }
}
