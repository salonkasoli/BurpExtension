package burp;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;


public class BurpExtender implements IBurpExtender, IContextMenuFactory{

	protected static final String HOST = "127.0.0.1";
	protected static final int PORT = 1337;
	private IBurpExtenderCallbacks callbacks;
	private PrintWriter stdout;
	private IExtensionHelpers helpers;
	

	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		
		this.callbacks = callbacks;
		this.helpers = callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        
		callbacks.setExtensionName("REQUEST SENDER");
		callbacks.registerContextMenuFactory(BurpExtender.this);		
	}
	
	//IContextMenu Implementation
	@Override
	public List<JMenuItem> createMenuItems(final IContextMenuInvocation invocation) {
		
		List<JMenuItem> menuItemList = new ArrayList<JMenuItem>();
		if ((invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_REQUEST)
				|| (invocation.getInvocationContext() == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST)){
			JMenuItem item = new JMenuItem(new AbstractAction("SEND IT"){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					IHttpRequestResponse[] requestArray = invocation.getSelectedMessages();
					Runnable r = new MyThread(requestArray[0]);
					new Thread(r).start();
				}
				
			});
			menuItemList.add(item);
		}
		
		
		return menuItemList;
	}
	
	public void sendToAnotherHost(IHttpRequestResponse request, String host,int  port){
		callbacks.makeHttpRequest(host, port, false, request.getRequest());
	}
	
	private class MyThread implements Runnable {

		private IHttpRequestResponse request;
		
		MyThread(IHttpRequestResponse inputRequest){
			request = inputRequest;
		}
		@Override
		public void run() {
			String host = "http://" + HOST +":" + PORT + "/";
			host = host + "?" + "host=" + request.getHttpService().getHost() + "&port=" + request.getHttpService().getPort();
			try {
				URL url = new URL(host);
				byte[] hostrequest = helpers.buildHttpRequest(url);
				try{
					byte[] response = callbacks.makeHttpRequest(HOST, PORT, false, hostrequest);
					if(response != null){
						IResponseInfo responseinfo = helpers.analyzeResponse(response);
						if(responseinfo.getStatusCode() == 200){
							callbacks.makeHttpRequest(HOST,  PORT, false, request.getRequest());
						}
					}
				} catch(Exception e){
					stdout.println("Server is unable");
				}
				
			} catch (MalformedURLException e) {
				stdout.println("URL CREATING ERROR");	
			}
		}
	}
}