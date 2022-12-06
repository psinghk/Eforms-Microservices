package in.nic.ashwini.eForms.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nic.ashwini.eForms.entities.EmailOtp;
import in.nic.ashwini.eForms.entities.MobileOtp;
import in.nic.ashwini.eForms.repositories.EmailOtpRepository;
import in.nic.ashwini.eForms.repositories.MobileOtpRepository;

@Service
public class MfaService {

	@Autowired
	private EmailOtpRepository emailOtpRepository;
	@Autowired
	private MobileOtpRepository mobileOtpRepository;
	@Autowired
	private HttpServletRequest request;
	
	public boolean verifyCode(Boolean govEmployee, String username, String mobile, int code) {
		LocalDateTime currentTime = LocalDateTime.now();
		Optional<MobileOtp> mobileOtpList = null;
		Optional<EmailOtp> emailOtpList = null;
		boolean flag = false;
		if (govEmployee) {
			mobileOtpList = mobileOtpRepository.findTopByMobileAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(mobile, currentTime, currentTime);
			MobileOtp mobileOtp = null;
			if (mobileOtpList.isPresent()) {
				mobileOtp = mobileOtpList.orElse(null);
				if (mobileOtp.getOtp() == code)
					flag = true;
			}
		} else {
			emailOtpList = emailOtpRepository.findTopByEmailAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(username, currentTime, currentTime);
			EmailOtp emailOtp = null;
			if (emailOtpList.isPresent()) {
				emailOtp = emailOtpList.orElse(null);
				if (emailOtp.getOtp() == code)
					flag = true;
			}
		}
		return flag;
	}

	public boolean isMobileOtpActive(String mobile) {
		LocalDateTime currentTime = LocalDateTime.now();
		Optional<MobileOtp> mobileOtp = mobileOtpRepository.findTopByMobileAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(mobile, currentTime, currentTime);
		if (mobileOtp.isPresent()) {
			return true;
		}
		return false;
	}

	public boolean isEmailOtpActive(String username) {
		LocalDateTime currentTime = LocalDateTime.now();
		Optional<EmailOtp> emailOtp = emailOtpRepository.findTopByEmailAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(username, currentTime, currentTime);
		if (emailOtp.isPresent()) {
			return true;
		}
		return false;
	}
	
	public String fetchMobileOtp(String mobile) {
		LocalDateTime currentTime = LocalDateTime.now();
		Optional<MobileOtp> mobileOtpList = mobileOtpRepository.findTopByMobileAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(mobile, currentTime, currentTime);
		MobileOtp mobileOtp = null;
		if (mobileOtpList.isPresent()) {
			mobileOtp = mobileOtpList.orElse(null);
			return mobileOtp.getOtp().toString();
		}
		return "OTP has expired. Please generate new OTP.";
	}
	
	public String fetchEmailOtp(String username) {
		LocalDateTime currentTime = LocalDateTime.now();
		Optional<EmailOtp> emailOtpList = emailOtpRepository.findTopByEmailAndGenerationTimeStampBeforeAndExpiryTimeStampAfterOrderByIdDesc(username, currentTime, currentTime);
		EmailOtp emailOtp = null;
		if (emailOtpList.isPresent()) {
			emailOtp = emailOtpList.orElse(null);
			return emailOtp.getOtp().toString();
		}
		return "OTP has expired. Please generate new OTP.";
	}

	public EmailOtp generateEmailOtp(String email) {
		EmailOtp emailOtp = new EmailOtp();
		LocalDateTime currentTime = LocalDateTime.now();
		emailOtp.setEmail(email);
		emailOtp.setOtp(ThreadLocalRandom.current().nextInt(100000, 1000000));
		emailOtp.setResendAttempt(0);
		emailOtp.setIp(request.getRemoteAddr());
		emailOtp.setNumberOfLoginAttempts(0);
		emailOtp.setGenerationTimeStamp(currentTime);
		emailOtp.setExpiryTimeStamp(currentTime.plusMinutes(30));
		emailOtp = emailOtpRepository.save(emailOtp);
		System.out.println("EMAIL OTP " + emailOtp);
		return emailOtp;
	}

	public MobileOtp generateMobileOtp(String mobile) {
		MobileOtp mobileOtp = new MobileOtp();
		LocalDateTime currentTime = LocalDateTime.now();
		mobileOtp.setMobile(mobile);
		mobileOtp.setOtp(ThreadLocalRandom.current().nextInt(100000, 1000000));
		mobileOtp.setResendAttempt(0);
		mobileOtp.setIp(request.getRemoteAddr());
		mobileOtp.setNumberOfLoginAttempts(0);
		mobileOtp.setGenerationTimeStamp(currentTime);
		mobileOtp.setExpiryTimeStamp(currentTime.plusMinutes(30));
		mobileOtp = mobileOtpRepository.save(mobileOtp);
		System.out.println("Mobile OTP " + mobileOtp);
		return mobileOtp;
	}

}
