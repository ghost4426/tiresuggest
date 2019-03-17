/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dto.Tire;
import dto.TireList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import utils.HibernateUtil;

/**
 *
 * @author Gian Tran
 */
public class TireDAO implements Serializable {

    private static Session session;
    private Transaction trans = null;
    private static SessionFactory factory = HibernateUtil.getSessionFactory();

    public void addTire(Tire tire) {
        Tire newTire = null;
        List<Tire> tires;
        if (tire.getSize().equals("")) {
            tires = getTireByName(tire);
            if (tires.size() == 0) {
                saveTire(tire);
                return;
            }
            if (tires.size() > 1) {
                return;
            }

            newTire = getTireByName(tire).get(0);
        } else {
            newTire = getTireBySizeAndBrand(tire);
        }
        if (newTire != null) {
            tire.setId(newTire.getId());
            if (!tire.getSize().equals("")) {
                updateTire(tire);
            }
        } else {
            saveTire(tire);
        }
    }

    public void saveTire(Tire tire) {
        try {
            session = factory.openSession();
            trans = session.beginTransaction();
            session.save(tire);
            trans.commit();
//            System.out.println("Add: " + tire.getName());
        } catch (HibernateException ex) {
            if (trans != null) {
                trans.rollback();
            }
            Logger.getLogger(TireDAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        } finally {
            session.close();
        }
    }

    public void updateTire(Tire tire) {
        try {
            session = factory.openSession();
            trans = session.beginTransaction();
            session.update(tire);
            trans.commit();
//            System.out.println("Update: " + tire.getName());
        } catch (HibernateException ex) {
            if (trans != null) {
                trans.rollback();
            }
            Logger.getLogger(TireDAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        } finally {
            session.close();
        }
    }

    public Tire getTireBySizeAndBrand(Tire tire) {
        Tire newTire = null;
        try {
            session = factory.openSession();
            trans = session.beginTransaction();
            String sql = "FROM Tire WHERE Brand = :brand AND Size = :size";
            Query query = session.createQuery(sql);
            query.setParameter("brand", tire.getBrand());
            query.setParameter("size", tire.getSize());
            newTire = (Tire) query.uniqueResult();
            trans.commit();
            return newTire;
        } catch (HibernateException ex) {
            if (trans != null) {
                trans.rollback();
            }
            Logger.getLogger(TireDAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        } finally {
            session.close();
        }
        return null;
    }

    public List<Tire> getTireByName(Tire tire) {
        List<Tire> tires = null;
        try {
            session = factory.openSession();
            trans = session.beginTransaction();
            String sql = "FROM Tire WHERE Name = :name";
            Query query = session.createQuery(sql);
            query.setParameter("name", tire.getName());
            tires = new ArrayList<>();
            tires = (List<Tire>) query.list();
            trans.commit();
        } catch (HibernateException ex) {
            if (trans != null) {
                trans.rollback();
            }
            Logger.getLogger(TireDAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        } finally {
            session.close();
        }
        return tires;

    }

    public List<Tire> getListTireBySize(String size) {
        List<Tire> tires = null;
        try {
            session = factory.openSession();
            trans = session.beginTransaction();
            String sql = "FROM Tire WHERE Size = :size";
            Query query = session.createQuery(sql);
            query.setParameter("size", size);
            tires = new ArrayList<>();
            tires = (List<Tire>) query.list();
        } catch (Exception ex) {
            if (trans != null) {
                trans.rollback();
            }
            Logger.getLogger(TireDAO.class.getName()).log(Level.SEVERE, ex.getMessage());
        } finally {
            session.close();
        }
        return tires;
    }

}
