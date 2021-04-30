package me.modernpage.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Slf4j
@Data
@AllArgsConstructor
public class ResponseUtils {

	private Integer code;


	private String msg;


	private Object data;

	/**
	 * Response to Json output
	 * 
	 * @param response
	 * @param data
	 */
	public static void responseJson(HttpServletResponse response, Object data, ObjectMapper objectMapper) {
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			out = response.getWriter();
			out.println(objectMapper.writeValueAsString(data));
			out.flush();
		} catch (Exception e) {
			log.error("Response output Json exception：" + e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * return info
	 * 
	 * @param code return code
	 * @param msg  return message
	 * @param data return data
	 * @return
	 */
	public static ResponseUtils response(Integer code, String msg, Object data) {
		return new ResponseUtils(code, msg, data);
	}

	/**
	 * return success
	 * 
	 * @param data
	 * @return
	 */
	public static ResponseUtils success(Object data) {
		return ResponseUtils.response(200, "Success", data);
	}

	/**
	 * return failure
	 * 
	 * @param data
	 * @return
	 */
	public static ResponseUtils fail(Object data) {
		return ResponseUtils.response(500, "Fail", data);
	}

}