package no.hvl.dat250.experiment1.polls;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

import no.hvl.dat250.experiment1.domain.Poll;
import no.hvl.dat250.experiment1.domain.User;
import no.hvl.dat250.experiment1.domain.Vote;
import no.hvl.dat250.experiment1.domain.VoteOption;

public class PollsTest {

    private EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("pollsPU");
        runInTx(emf, this::populate);
    }

    private static void runInTx(EntityManagerFactory emf, Consumer<EntityManager> work) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            work.accept(em);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    private void populate(EntityManager em) {
        User alice = new User("alice", "alice@online.com");
        User bob   = new User("bob",   "bob@bob.home");
        User eve   = new User("eve",   "eve@mail.org");
        em.persist(alice);
        em.persist(bob);
        em.persist(eve);

        // Alice's poll
        Poll poll1 = alice.createPoll("Vim or Emacs?");
        VoteOption vim   = poll1.addVoteOption("Vim"); // order 0
        VoteOption emacs = poll1.addVoteOption("Emacs"); // order 1

        // Eve's poll
        Poll poll2 = eve.createPoll("Pineapple on Pizza");
        VoteOption yes = poll2.addVoteOption("Yes! Yammy!"); // 0
        VoteOption no  = poll2.addVoteOption("Mamma mia: Nooooo!"); // 1

        em.persist(poll1);
        em.persist(poll2);

        em.persist(alice.voteFor(vim));
        em.persist(bob.voteFor(vim));
        em.persist(eve.voteFor(emacs));
        em.persist(eve.voteFor(yes));
    }

    @Test
    void testUsers() {
        runInTx(emf, em -> {
            Long count = em.createQuery("select count(u) from User u", Long.class).getSingleResult();
            assertEquals(3L, count);

            User maybeBob = em.createQuery("select u from User u where u.username = :name", User.class)
                    .setParameter("name", "bob")
                    .getSingleResult();
            assertNotNull(maybeBob);
        });
    }

    @Test
    void testVotes() {
        runInTx(emf, em -> {
            Long vimVotes = em.createQuery(
                    "select count(v) from Vote v " +
                    "join v.voteOption o join o.poll p join p.creator u " +
                    "where u.email = :mail and o.presentationOrder = :ord", Long.class)
                .setParameter("mail", "alice@online.com")
                .setParameter("ord", 0)
                .getSingleResult();

            Long emacsVotes = em.createQuery(
                    "select count(v) from Vote v " +
                    "join v.voteOption o join o.poll p join p.creator u " +
                    "where u.email = :mail and o.presentationOrder = :ord", Long.class)
                .setParameter("mail", "alice@online.com")
                .setParameter("ord", 1)
                .getSingleResult();

            assertEquals(2L, vimVotes);
            assertEquals(1L, emacsVotes);
        });
    }

    @Test
    void testOptions() {
        runInTx(emf, em -> {
            List<String> options = em.createQuery(
                    "select o.caption from Poll p join p.voteOptions o " +
                    "join p.creator u where u.email = :mail order by o.presentationOrder",
                    String.class)
                .setParameter("mail", "eve@mail.org")
                .getResultList();

            assertEquals(Arrays.asList("Yes! Yammy!", "Mamma mia: Nooooo!"), options);
        });
    }
}