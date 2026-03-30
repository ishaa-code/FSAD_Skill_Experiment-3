package com.inventory.Skill_3;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class App {

    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        insertProducts(session);

        System.out.println("\n===== PRODUCTS SORTED BY PRICE ASC =====");
        List<Product> ascProducts = session
                .createQuery("from Product order by price asc", Product.class)
                .list();
        ascProducts.forEach(System.out::println);

        System.out.println("\n===== PRODUCTS SORTED BY PRICE DESC =====");
        List<Product> descProducts = session
                .createQuery("from Product order by price desc", Product.class)
                .list();
        descProducts.forEach(System.out::println);

        System.out.println("\n===== PRODUCTS SORTED BY QUANTITY DESC =====");
        List<Product> quantityProducts = session
                .createQuery("from Product order by quantity desc", Product.class)
                .list();
        quantityProducts.forEach(System.out::println);

        System.out.println("\n===== PAGINATION: FIRST 3 PRODUCTS =====");
        Query<Product> firstPageQuery = session.createQuery("from Product", Product.class);
        firstPageQuery.setFirstResult(0);
        firstPageQuery.setMaxResults(3);
        firstPageQuery.list().forEach(System.out::println);

        System.out.println("\n===== PAGINATION: NEXT 3 PRODUCTS =====");
        Query<Product> secondPageQuery = session.createQuery("from Product", Product.class);
        secondPageQuery.setFirstResult(3);
        secondPageQuery.setMaxResults(3);
        secondPageQuery.list().forEach(System.out::println);

        System.out.println("\n===== AGGREGATE: TOTAL PRODUCTS =====");
        Long totalProducts = session
                .createQuery("select count(*) from Product", Long.class)
                .uniqueResult();
        System.out.println("Total Products = " + totalProducts);

        System.out.println("\n===== AGGREGATE: PRODUCTS WHERE QUANTITY > 0 =====");
        Long availableProducts = session
                .createQuery("select count(*) from Product where quantity > 0", Long.class)
                .uniqueResult();
        System.out.println("Products with quantity > 0 = " + availableProducts);

        System.out.println("\n===== AGGREGATE: COUNT GROUPED BY DESCRIPTION =====");
        List<Object[]> groupedCount = session
                .createQuery("select description, count(*) from Product group by description", Object[].class)
                .list();
        for (Object[] row : groupedCount) {
            System.out.println("Description = " + row[0] + ", Count = " + row[1]);
        }

        System.out.println("\n===== AGGREGATE: MIN AND MAX PRICE =====");
        Object[] minMax = session
                .createQuery("select min(price), max(price) from Product", Object[].class)
                .uniqueResult();
        System.out.println("Minimum Price = " + minMax[0]);
        System.out.println("Maximum Price = " + minMax[1]);

        System.out.println("\n===== GROUP BY DESCRIPTION =====");
        List<Object[]> groupedData = session
                .createQuery("select description, count(*), avg(price) from Product group by description", Object[].class)
                .list();
        for (Object[] row : groupedData) {
            System.out.println("Description = " + row[0] + ", Count = " + row[1] + ", Avg Price = " + row[2]);
        }

        System.out.println("\n===== WHERE: PRODUCTS IN PRICE RANGE 1000 TO 50000 =====");
        Query<Product> rangeQuery = session.createQuery(
                "from Product where price between :min and :max", Product.class);
        rangeQuery.setParameter("min", 1000.0);
        rangeQuery.setParameter("max", 50000.0);
        rangeQuery.list().forEach(System.out::println);

        System.out.println("\n===== LIKE: NAMES STARTING WITH 'M' =====");
        session.createQuery("from Product where name like 'M%'", Product.class)
                .list()
                .forEach(System.out::println);

        System.out.println("\n===== LIKE: NAMES ENDING WITH 'r' =====");
        session.createQuery("from Product where name like '%r'", Product.class)
                .list()
                .forEach(System.out::println);

        System.out.println("\n===== LIKE: NAMES CONTAINING 'top' =====");
        session.createQuery("from Product where name like '%top%'", Product.class)
                .list()
                .forEach(System.out::println);

        System.out.println("\n===== LIKE: NAMES WITH EXACTLY 5 CHARACTERS =====");
        session.createQuery("from Product where name like '_____'", Product.class)
                .list()
                .forEach(System.out::println);

        session.close();
        HibernateUtil.getSessionFactory().close();
    }

    private static void insertProducts(Session session) {
        Transaction tx = session.beginTransaction();

        Long count = session.createQuery("select count(*) from Product", Long.class).uniqueResult();

        if (count == 0) {
            session.persist(new Product("Laptop", "Electronics", 60000, 5));
            session.persist(new Product("Mouse", "Accessories", 500, 20));
            session.persist(new Product("Keyboard", "Accessories", 1500, 10));
            session.persist(new Product("Phone", "Electronics", 30000, 8));
            session.persist(new Product("Monitor", "Electronics", 12000, 6));
            session.persist(new Product("Charger", "Accessories", 800, 15));
            session.persist(new Product("Tablet", "Electronics", 25000, 4));
            session.persist(new Product("Speaker", "Accessories", 3500, 7));
            System.out.println("Sample products inserted successfully.");
        } else {
            System.out.println("Products already exist. Skipping insertion.");
        }

        tx.commit();
    }
}