package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.CartBean;
import model.CartModel;
import model.PreferitiModel;
import model.ProductBean;
import model.ProductModel;

@WebServlet("/ProductControl")
/**
 * Servlet implementation class ProductControl
 */
public class ProductControl extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    static ProductModel model;
    
    static {
        model = new ProductModel();
    }
    
    public ProductControl() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            
            if (action != null && action.compareTo("dettaglio") == 0) {
                handleDettaglio(request, response);
            } else if (action != null && action.compareTo("elimina") == 0) {
                handleElimina(request, response);
            } else if (action != null && action.compareTo("modificaForm") == 0) {
                handleModificaForm(request, response);
            } else if (action != null && action.compareTo("modifica") == 0) {
                handleModifica(request, response);
            } else {
                handleDefault(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            request.setAttribute("errorMessage", e.getMessage());
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/errorPage.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void handleDettaglio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String codiceStr = request.getParameter("codice");
        int codice = Integer.parseInt(codiceStr);
        
        ProductBean prodotto = model.doRetrieveByKey(codice);
        request.setAttribute("prodottoDettaglio", prodotto);

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/productDetail.jsp");
        dispatcher.forward(request, response);
    }

    private void handleElimina(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        @SuppressWarnings("unchecked")
        Collection<ProductBean> lista = (Collection<ProductBean>) request.getSession().getAttribute("products");
        int codice = Integer.parseInt(request.getParameter("codice"));
        Collection<ProductBean> collezione = model.deleteProduct(codice, lista);
        
        request.getSession().removeAttribute("products");
        request.getSession().setAttribute("products", collezione);
        request.getSession().setAttribute("refreshProduct", true);

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/ProductsPage.jsp");
        dispatcher.forward(request, response);
    }

    private void handleModificaForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        ProductBean bean = model.doRetrieveByKey(Integer.parseInt(request.getParameter("codice")));
        request.setAttribute("updateProd", bean);

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/modifica-prodotto.jsp");
        dispatcher.forward(request, response);
    }

    private void handleModifica(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        ProductBean bean = new ProductBean();
        bean.setCodice(Integer.parseInt(request.getParameter("codice")));
        bean.setNome(request.getParameter("nome"));
        bean.setDescrizione(request.getParameter("descrizione"));
        bean.setPrezzo(Double.parseDouble(request.getParameter("prezzo")));
        bean.setSpedizione(Double.parseDouble(request.getParameter("spedizione")));
        bean.setTag(request.getParameter("tag"));
        bean.setTipologia(request.getParameter("tipologia"));

        model.updateProduct(bean);
        updateSessionAttributes(request, bean);
        
        request.getSession().setAttribute("refreshProduct", true);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    private void handleDefault(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String tipologia = (String) request.getSession().getAttribute("tipologia");

        request.removeAttribute("products");
        request.setAttribute("products", model.doRetrieveAll(tipologia));

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/ProductsPage.jsp?tipologia=" + tipologia);
        dispatcher.forward(request, response);
    }

    private void updateSessionAttributes(HttpServletRequest request, ProductBean bean) throws SQLException {
        if (request.getSession().getAttribute("carrello") != null) {
            CartModel cartmodel = new CartModel();
            CartBean newCart = cartmodel.updateCarrello(bean, (CartBean) request.getSession().getAttribute("carrello"));
            request.getSession().setAttribute("carrello", newCart);
        }
        if (request.getSession().getAttribute("preferiti") != null) {
            PreferitiModel preferitiModel = new PreferitiModel();
            @SuppressWarnings("unchecked")
            Collection<ProductBean> lista = preferitiModel.updatePreferiti(bean, (Collection<ProductBean>) request.getSession().getAttribute("preferiti"));
            request.getSession().setAttribute("preferiti", lista);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
