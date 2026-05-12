package org.milana.weblab3.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.cfg.Configuration;

import java.util.List;

public class DataBaseController {
    private SessionFactory sessionFactory;
    private Session session;

    public DataBaseController() {
        try {
            this.sessionFactory = new Configuration()
                    .configure("META-INF/hibernate.cfg.xml")
                    .addAnnotatedClass(HitResult.class)
                    .buildSessionFactory();

            this.createSession();
        } catch (Exception e) {
            System.out.println("MEOW MEOW: " + e.getMessage());
        }
    }

    private void createSession() {
        this.session = sessionFactory.getCurrentSession();
    }

    public List<HitResult> getUserHits(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) return null;

        createSession();
        this.session.beginTransaction();

        String req = "SELECT hit FROM HitResult hit WHERE hit.sessionId = :sessionId AND hit.removed=false";

        List<HitResult> results = this.session.createQuery(req, HitResult.class)
                .setParameter("sessionId", sessionId).getResultList();

        this.session.getTransaction().commit();

        System.out.println("Get hits MEOW from database: " + results.size());

        return results;
    }

    public void addHitResult(HitResult hitResult) {
        System.out.println("check MEOW");
        if (hitResult == null) return;
        createSession();

        System.out.println("Starting saving hit");

        this.session.beginTransaction();
        this.session.save(hitResult);
        this.session.getTransaction().commit();

        System.out.println("Saved hit MEOW");
    }

    public void markUserHitsRemoved(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) return;
        createSession();

        this.session.beginTransaction();
        this.session.createQuery("UPDATE HitResult SET removed=true WHERE sessionId = :sessionId")
                .setParameter("sessionId", sessionId).executeUpdate();
        this.session.getTransaction().commit();
    }
}
