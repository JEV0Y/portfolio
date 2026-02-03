package model;

import model.interfaces.FullInformation;

public class PhoneNumber implements Comparable<PhoneNumber>, FullInformation {
	private final String countryCode, areaCode, localNumber;

	public PhoneNumber(String countryCode, String areaCode, String localNumber) {
		/*
		 * attributes declared as final must be set in the constructor; once set, they
		 * cannot be changed.
		 */
		this.countryCode = countryCode;
		this.areaCode = areaCode;
		this.localNumber = localNumber;
	}

	@Override
	public int compareTo(PhoneNumber pn) {
		return toString().compareTo(pn.toString());
	}

	@Override
	public String toString() {
		return countryCode + "-" + areaCode + "-" + localNumber;
	}


	public static boolean isValid(String phoneNumber) {
		if (phoneNumber == null)
			return false;

		boolean hasUnexpectedChars = false;
		for (int i = 0; i < phoneNumber.length(); i++)
			switch (phoneNumber.charAt(i) + "") {
			case "0":
			case "1":
			case "2":
			case "3":
			case "4":
			case "5":
			case "6":
			case "7":
			case "8":
			case "9":
			case "-":
				break;
			default:
				hasUnexpectedChars = true;
			}

		if (hasUnexpectedChars || !(phoneNumber.length() == 12))
			return false;

		String p[] = phoneNumber.split("-");
		return p.length == 3 && p[0].length() == 3 && p[1].length() == 3 && p[2].length() == 4;
	}

	@Override
	public String getFullInformation() {
		return toString();
	}
}
