package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:schema.sql")

public class UserDaoImplTest {

    private static final long USER_ID = 1;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String MAIL = "mail";
    private static final boolean IS_ADMIN = true;
    @Autowired
    private DataSource ds;
    @Autowired
    private UserDaoJdbc userDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }
    @Test
    public void testCreate(){
        //Ejercitar
        final User u = userDao.createUser(USERNAME, PASSWORD, MAIL,IS_ADMIN);
        //Asserts
        Assert.assertTrue(u.getId() > 0);
        Assert.assertEquals(USERNAME,u.getUserName());
        Assert.assertEquals(PASSWORD,u.getPassword());
        Assert.assertEquals(MAIL,u.getMailAddress());
        Assert.assertEquals(IS_ADMIN,u.isAdmin());
    }
    @Test
    public void testGetUser(){
        //Setup
        jdbcTemplate.execute(String.format("INSERT INTO users(id, username, password, mail,isAdmin) VALUES( %d,'%s','%s','%s',%b)", USER_ID, USERNAME, PASSWORD, MAIL,IS_ADMIN));

        //Ejercitar
        final User user = userDao.getUserById(USER_ID);
        //Asserts
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getUserName(),USERNAME);
        Assert.assertEquals(user.getPassword(), PASSWORD);
        Assert.assertEquals(user.getMailAddress(), MAIL);
        Assert.assertEquals(user.getId(), USER_ID);
        Assert.assertEquals(IS_ADMIN,user.isAdmin());
    }
}
