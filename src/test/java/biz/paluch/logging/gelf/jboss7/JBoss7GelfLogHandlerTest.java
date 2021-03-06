package biz.paluch.logging.gelf.jboss7;

import static biz.paluch.logging.gelf.jboss7.JBoss7LogTestUtil.getJBoss7GelfLogHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;

import biz.paluch.logging.gelf.GelfTestSender;
import biz.paluch.logging.gelf.intern.GelfMessage;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 27.09.13 08:36
 */
public class JBoss7GelfLogHandlerTest {

    @Before
    public void before() throws Exception {
        GelfTestSender.getMessages().clear();

        LogManager.getLogManager().reset();

        MDC.remove("mdcField1");
    }

    @Test
    public void testSimple() throws Exception {

        JBoss7GelfLogHandler handler = getJBoss7GelfLogHandler();

        Logger logger = Logger.getLogger(getClass().getName());
        logger.addHandler(handler);

        logger.info("Blubb Test");
        assertEquals(1, GelfTestSender.getMessages().size());

        GelfMessage gelfMessage = GelfTestSender.getMessages().get(0);

        assertEquals("Blubb Test", gelfMessage.getFullMessage());
        assertEquals("6", gelfMessage.getLevel());
        assertEquals("Blubb Test", gelfMessage.getShortMessage());
        assertEquals(8192, gelfMessage.getMaximumMessageSize());

    }

    @Test
    public void testSimpleWithMsgFormatSubstitution() throws Exception {

        JBoss7GelfLogHandler handler = getJBoss7GelfLogHandler();

        Logger logger = Logger.getLogger(getClass().getName());
        logger.addHandler(handler);

        logger.log(Level.INFO, "Blubb Test {0}", new String[] { "aaa" });
        assertEquals(1, GelfTestSender.getMessages().size());

        GelfMessage gelfMessage = GelfTestSender.getMessages().get(0);

        assertEquals("Blubb Test aaa", gelfMessage.getFullMessage());
        assertEquals("6", gelfMessage.getLevel());
        assertEquals("Blubb Test aaa", gelfMessage.getShortMessage());
        assertEquals(8192, gelfMessage.getMaximumMessageSize());

    }

    @Test
    public void testSimpleWithStringFormatSubstitution() throws Exception {

        JBoss7GelfLogHandler handler = getJBoss7GelfLogHandler();

        Logger logger = Logger.getLogger(getClass().getName());
        logger.addHandler(handler);

        logger.log(Level.INFO, "Blubb Test %s", new String[] { "aaa" });
        assertEquals(1, GelfTestSender.getMessages().size());

        GelfMessage gelfMessage = GelfTestSender.getMessages().get(0);

        assertEquals("Blubb Test aaa", gelfMessage.getFullMessage());
        assertEquals("6", gelfMessage.getLevel());
        assertEquals("Blubb Test aaa", gelfMessage.getShortMessage());
        assertEquals(8192, gelfMessage.getMaximumMessageSize());

    }

    @Test
    public void testFields() throws Exception {

        JBoss7GelfLogHandler handler = getJBoss7GelfLogHandler();

        Logger logger = Logger.getLogger(getClass().getName());
        logger.addHandler(handler);

        MDC.put("mdcField1", "a value");

        logger.info("Blubb Test");
        assertEquals(1, GelfTestSender.getMessages().size());

        GelfMessage gelfMessage = GelfTestSender.getMessages().get(0);

        assertEquals("fieldValue1", gelfMessage.getField("fieldName1"));
        assertEquals("fieldValue2", gelfMessage.getField("fieldName2"));
        assertEquals("a value", gelfMessage.getField("mdcField1"));
        assertNull(gelfMessage.getField("mdcField2"));

    }


}
