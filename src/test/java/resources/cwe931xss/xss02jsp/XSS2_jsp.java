package resources.cwe931xss.xss02jsp;

import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class XSS2_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

	private static final long serialVersionUID = 1L;

	private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

	private static java.util.List<String> _jspx_dependants;

	static {
		_jspx_dependants = new java.util.ArrayList<String>(1);
		_jspx_dependants.add("/test/XSS/../include/XSS2Include.jsp");
	}

	@Override
	public List<String> getDependants() {
		return _jspx_dependants;
	}

	@Override
	public void _jspInit() {

	}

	@Override
	public void _jspDestroy() {
	}

	@SuppressWarnings({ "unused", "resource" })
	@Override
	public void _jspService(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException,
			ServletException {

		PageContext pageContext = null;
		HttpSession session = null;
		ServletContext application = null;
		ServletConfig config = null;
		JspWriter out = null;
		Object page = this;
		JspWriter _jspx_out = null;
		PageContext _jspx_page_context = null;

		try {
			response.setContentType("text/html; charset=ISO-8859-1");
			pageContext = _jspxFactory.getPageContext(this, request, response, null, true, 8192, true);
			_jspx_page_context = pageContext;
			application = pageContext.getServletContext();
			config = pageContext.getServletConfig();
			session = pageContext.getSession();
			out = pageContext.getOut();

			out.write("\r\n<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n<html>\r\n<head>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\r\n<title></title>\r\n</head>\r\n<body>\r\n\r\n");
			out.write("\r\n\r\n");
			String inputStr = request.getParameter("a1");
			out.write("\r\n\r\n");
			out.print(inputStr);
			out.write("\r\n\r\n</body>\r\n</html>");
		} catch (Throwable t) {
			if (!(t instanceof SkipPageException)) {
				if (out != null && out.getBufferSize() != 0) {
					try {
						out.clearBuffer();
					} catch (java.io.IOException e) {
					}
				}
				if (_jspx_page_context != null) {
					_jspx_page_context.handlePageException(t);
				}
			}
		} finally {
			_jspxFactory.releasePageContext(_jspx_page_context);
		}
	}
}
