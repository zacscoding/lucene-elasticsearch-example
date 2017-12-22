package com.estest.util;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;

/**
 * Util for display information 
 * 
 * @author zaccoding
 * @Date 2017. 8. 2.
 */
public class LoggerUtils {	
	/**
	 * Display Page information
	 * 
	 * @author zaccoding
	 * @Data 2017. 8. 2.
	 * @param logger 
	 * @param prefix
	 * @param page
	 */
	public static void displayPageInfo(Logger logger, String prefix, Page<?> page) {
		logger.debug(prefix);
		logger.debug("## [page inform] getTotalElements() : {} |  getTotalPages() : {} | ", new Object[]{page.getTotalElements(),page.getTotalPages()}); 
	}

}
