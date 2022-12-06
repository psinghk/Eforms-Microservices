package in.nic.eForms.utils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;


public class Test {

	
	public static void main(String args[]) {
		LocalDate date1 = LocalDate.of(2000, 1, 1);
		 Date dobdate = java.sql.Date.valueOf(date1);

		 LocalDate date2 = LocalDate.of(2067, 1, 1);
		 Date dordate = java.sql.Date.valueOf(date2);
		 
		
			
			Calendar dob = Calendar.getInstance();
			Calendar dor = Calendar.getInstance();
			Calendar future = Calendar.getInstance();
	        Calendar past = Calendar.getInstance();
	        
			dob.setTime(dobdate);
			future.setTime(dobdate);
			past.setTime(dobdate);
			System.out.println("dob::::::"+dob);
			dor.setTime(dordate);
			System.out.println("dor::::::"+dor);
			future.add(Calendar.YEAR, 67);
			System.out.println("future::::::"+future);
			past.add(Calendar.YEAR, 18);
			System.out.println("past::::::"+past);
			
			
			System.out.println(":::1:::"+dor.equals(future));
			System.out.println(":::2:::"+dor.before(past));
			System.out.println(":::3:::"+dor.after(future));
			System.out.println(":::4:::"+dor.equals(past));
			if((dor.equals(future)) || (dor.before(past))){
				System.out.println(":::1:::");
			}
			if((dor.equals(future)) || (dor.after(future))){
				System.out.println(":::2:::");
			}

	}
}
