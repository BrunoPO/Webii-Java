/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.cliente;

import entidades.cliente.Cliente;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import util.GenericDAO;
import util.HibernateUtil;

/**
 *
 * @author prog
 */
public class ClienteDAO extends GenericDAO<Cliente> {

    public ClienteDAO() {
        super(Cliente.class);
    }
    
    public Cliente validateCliente(String login,String senha) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        
        /*CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Cliente> criteriaQuery = criteriaBuilder.createQuery(Cliente.class);
        Root<Cliente> rootCliente = criteriaQuery.from(Cliente.class);
        */
        Criteria cr = session.createCriteria(Cliente.class);
        cr.add(Restrictions.eq("login", login.toLowerCase()));
        cr.add(Restrictions.eq("senha", senha.toLowerCase()));
        List<Cliente> clientes = cr.list();
        
        //criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.like(criteriaBuilder.lower(rootCliente.get("login")), "%" + login.toLowerCase() + "%"),criteriaBuilder.like(criteriaBuilder.lower(rootCliente.get("senha")), "%" + senha.toLowerCase() + "%")));
        //List<Cliente> clientes = (List<Cliente>) session.createQuery(criteriaQuery).getResultList();
        System.out.println(clientes);
        System.out.println(clientes.get(0).getID());
        transaction.commit();
        session.close();
        if(clientes.size()>0)
            return (Cliente) clientes.get(0);
        else
            return null;
    }
    
    public Cliente getByCodigo(Integer codigo) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Cliente cliente = (Cliente) session.get(Cliente.class, codigo);
        transaction.commit();
        session.close();
        return cliente;
    }
    
    public List<Cliente> getByNome(String nome) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        /*CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Cliente> criteriaQuery = criteriaBuilder.createQuery(Cliente.class);
        Root<Cliente> rootCliente = criteriaQuery.from(Cliente.class);
        criteriaQuery.where(criteriaBuilder.like(criteriaBuilder.lower(rootCliente.get("nome")), "%" + nome.toLowerCase() + "%"));
        List<Cliente> list = (List<Cliente>) session.createQuery(criteriaQuery).getResultList();
        */
        Criteria cr = session.createCriteria(Cliente.class);
        cr.add(Restrictions.eq("nome", nome.toLowerCase()));
        List<Cliente> list = cr.list();
        
        transaction.commit();
        session.close();
        return list;
    }
}


