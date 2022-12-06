package in.nic.ashwini.eForms.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.nic.ashwini.eForms.models.OauthBean;
import in.nic.ashwini.eForms.models.OauthOtpBean;
import in.nic.ashwini.eForms.models.SsoResponseBean;
import in.nic.ashwini.eForms.services.CaptchaService;
import in.nic.ashwini.eForms.services.SSODecodeString;
import in.nic.ashwini.eForms.services.UtilityService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AuthController {

	@Autowired
	public ResponseBean responseBean;

	@Autowired
	private UtilityService utilityService;

	@Autowired
	private CaptchaService captchaService;

//	@Autowired
//	private SSODecodeString ssoDecodeString;

	// @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
	@GetMapping("/test")
	public String test() {
		return "Test";
	}

	@PostMapping("/test-url")
	public String testPost() {
		return "Test";
	}

	@GetMapping("/verifyUser")
	public ResponseBean verifyUser(@RequestParam @NotBlank @Email final String username) {
		log.info("Verification of user {} started", username);
		responseBean.setEmail(username);
		responseBean.setGovEmployee(utilityService.isGovEmployee(username));
		if (utilityService.isUserRegistered(username)) {
			responseBean.setRegisteredUser(true);
		} else {
			responseBean.setRegisteredUser(false);
		}
		return responseBean;
	}

	@GetMapping("/callbackUrlForParichay")
	public OAuth2AccessToken callbackUrlForParichay(@RequestParam @NotBlank final String string,
			HttpServletRequest request) {
		log.info("Parichay has called the callback URL");
		// Decode it and fetch all the details like session ID, BrowserID, username etc
		try {
			String jsonResponse = SSODecodeString.decyText(string, "BKQTuiOioGSZev8LGX0kKvUrcRrxf0zF");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			SsoResponseBean ssoResponse = mapper.readValue(jsonResponse, SsoResponseBean.class);
			if (ssoResponse.getStatus().equalsIgnoreCase("success")) {
				String ip = fetchClientIp(request);
				// if (ip.equalsIgnoreCase(ssoResponse.getIp())) {
				if (utilityService
						.isSessionValid(ssoResponse.getBrowserId(), ssoResponse.getSessionId(),
								ssoResponse.getLocalTokenId(), ssoResponse.getUserName(), "eforms")
						.equalsIgnoreCase("true"))
					return utilityService.generateTokenForSSOUsers(ssoResponse.getUa(), ssoResponse.getBrowserId(),
							ssoResponse.getSessionId(), ssoResponse.getLocalTokenId(), ssoResponse.getUserName(),
							ssoResponse.getMobileNo(), ssoResponse.getIp());
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@PostMapping("/refreshToken")
	public OAuth2AccessToken refreshToken(@RequestParam @NotBlank final String token, HttpServletRequest request) {
		log.info("Calling refresh Toekn");
		try {

			String[] parts = token.split("\\.", 0);

			byte[] bytes = Base64.getUrlDecoder().decode(parts[1]);
			String decodedString = new String(bytes, StandardCharsets.UTF_8);

			System.out.println("Decoded: " + decodedString);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = null;

			try {
				// convert JSON string to Map
				map = mapper.readValue(decodedString, new TypeReference<Map<String, Object>>() {
				});
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

			String username = map.get("username").toString().trim();
			String localTokenId = map.get("localTokenId").toString().trim();
			String serviceName = map.get("service").toString().trim();
			String browserId = map.get("browserId").toString().trim();
			String sessionId = map.get("sessionId").toString().trim();
			String ip = map.get("ip").toString().trim();
			String clientIp = fetchClientIp(request);
			// if (clientIp.equalsIgnoreCase(ip)) {
			if (utilityService.isSessionValid(browserId, sessionId, localTokenId, username, serviceName)
					.equalsIgnoreCase("true"))
				return utilityService.generateRefreshToken(token);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@PostMapping("/authenticate")
	public OAuth2AccessToken authenticate(@RequestBody @Valid OauthBean oauthBean, HttpServletRequest request) {
		log.info("Calling Authenticate Api");
		String clientIp = fetchClientIp(request);
		return utilityService.authenticateUserThroughOauth(oauthBean.getUsername(), oauthBean.getPassword(),
				oauthBean.getAppName(), clientIp);
	}
	
	@PostMapping("/validateOtp")
	public OAuth2AccessToken validateOtp(@RequestBody @Valid OauthOtpBean oauthOtpBean, HttpServletRequest request) {
		System.out.println("zull validateOtp");
		log.info("Calling Authenticate Api");
		String clientIp = fetchClientIp(request);
		return utilityService.validateOtp(oauthOtpBean.getToken(), oauthOtpBean.getOtp(), oauthOtpBean.getService(), clientIp);
	}

	@GetMapping("/captcha")
	public String generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("Captcha generation code started");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Max-Age", 0);
		Color textColor = new Color(0x47, 0x6D, 0xAB);
		Font textFont = new Font("Arial", Font.PLAIN, 24);
		int charsToPrint = 6;
		int width = 150;
		int height = 50;
		float horizMargin = 30.0f;
		float imageQuality = 1.0f; // max is 1.0 (this is for jpeg)
		double rotationRange = 1.1; // this is radians

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		GradientPaint gp = new GradientPaint(10, 10, Color.WHITE, 20, 20, Color.WHITE, true);
		g.setPaint(gp);
		// Draw an oval
		// g.setColor(new Color(0xD8, 0xD5, 0x88));
		g.fillRect(0, 0, width, height);
		g.setColor(textColor);
		g.setFont(textFont);

		FontMetrics fontMetrics = g.getFontMetrics();
		int maxAdvance = fontMetrics.getMaxAdvance();
		int fontHeight = fontMetrics.getHeight();
		String elegibleChars = "ABCDEFGHJKLMPQRSTUVWXYabcdefhjkmnpqrstuvwxy23456789";
		char[] chars = elegibleChars.toCharArray();
		float spaceForLetters = -horizMargin * 2 + width;
		float spacePerChar = spaceForLetters / (charsToPrint - 1.0f);

		StringBuffer finalString = new StringBuffer();

		for (int i = 0; i < charsToPrint; i++) {
			double randomValue = Math.random();
			int randomIndex = (int) Math.round(randomValue * (chars.length - 1));
			char characterToShow = chars[randomIndex];
			finalString.append(characterToShow);

			// this is a separate canvas used for the character so that
			// we can rotate it independently
			int charWidth = fontMetrics.charWidth(characterToShow);
			int charDim = Math.max(maxAdvance, fontHeight);
			int halfCharDim = (int) (charDim / 2);
			BufferedImage charImage = new BufferedImage(charDim, charDim, BufferedImage.TYPE_INT_ARGB);
			Graphics2D charGraphics = charImage.createGraphics();
			charGraphics.translate(halfCharDim, halfCharDim);
			double angle = (Math.random() - 0.5) * rotationRange;
			charGraphics.transform(AffineTransform.getRotateInstance(angle));
			charGraphics.translate(-halfCharDim, -halfCharDim);
			charGraphics.setColor(textColor);
			charGraphics.setFont(textFont);
			int charX = (int) (0.5 * charDim - 0.5 * charWidth);
			charGraphics.drawString("" + characterToShow, charX,
					(int) ((charDim - fontMetrics.getAscent()) / 2 + fontMetrics.getAscent()));
			float x = horizMargin + spacePerChar * (i) - charDim / 2.0f;
			int y = (int) ((height - charDim) / 2);
			g.drawImage(charImage, (int) x, y, charDim, charDim, null, null);
			charGraphics.dispose();
		}

		String captcha = finalString.toString();
		Cookie cookie = new Cookie("captcha", captcha);
		cookie.setMaxAge(60 * 60); // sets expiration after one hour
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
		Iterator iter = ImageIO.getImageWritersByFormatName("JPG");

		if (iter.hasNext()) {
			ImageWriter writer = (ImageWriter) iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(imageQuality);
			writer.setOutput(ImageIO.createImageOutputStream(response.getOutputStream()));
			IIOImage imageIO = new IIOImage(bufferedImage, null, null);
			writer.write(null, imageIO, iwp);
		} else {
			throw new RuntimeException("no encoder found for jsp");
		}
		return captcha;
	}

	@PreAuthorize("hasRole('GOV_USER')")
	@GetMapping("/isGovUser")
	public Map<String, Boolean> isGovUser() {
		Map<String, Boolean> response = new HashMap<>();
		response.put("isGovUser", true);
		return response;
	}

	@PreAuthorize("hasRole('OLD_USER')")
	@GetMapping("/isOldUser")
	public Map<String, Boolean> isOldUser() {
		Map<String, Boolean> response = new HashMap<>();
		response.put("isOldUser", true);
		return response;
	}

	@GetMapping("/eform-services")
	public Map<String, Object> fetchService() {
		return utilityService.fetchService();
	}

	private String fetchClientIp(HttpServletRequest request) {
		String remoteAddr = "";
		if (request != null) {
			remoteAddr = request.getHeader(FilterConstants.X_FORWARDED_FOR_HEADER);
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			} else {
				remoteAddr = new StringTokenizer(remoteAddr, ",").nextToken().trim();
			}
		}
		return remoteAddr;
	}

	// @GetMapping("/isRequestEsignedByUser")
//	public Boolean isRequestEsignedByUser(@RequestParam("regNumber") @NotEmpty String regNumber) {
//		if (utilityService.isRequestEsignedByUser(regNumber)) {
//			return true;
//		}
//		return false;
//	}
}
