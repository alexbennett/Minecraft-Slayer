package net.alexben.Slayer.Utilities;

import java.util.Random;

/**
 * Utility for all object-related methods.
 */
public class ObjUtil
{
	/**
	 * Capitalizes and returns <code>input</code>.
	 * 
	 * @param input the string to capitalize.
	 * @return String
	 */
	public static String capitalize(String input)
	{
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	/**
	 * Converts <code>object</code> to a float.
	 * 
	 * @param object the object to convert.
	 * @return float
	 */
	public static float toFloat(Object object)
	{
		return Float.parseFloat(object.toString());
	}

	/**
	 * Converts <code>object</code> to an integer.
	 * 
	 * @param object the object to convert.
	 * @return int
	 */
	public static int toInteger(Object object)
	{
		return Integer.parseInt(object.toString());
	}

	/**
	 * Converts <code>object</code> to a long.
	 * 
	 * @param object the object to convert.
	 * @return long
	 */
	public static long toLong(Object object)
	{
		return Long.parseLong(object.toString());
	}

	/**
	 * Converts <code>object</code> to a double.
	 * 
	 * @param object the object to convert.
	 * @return double
	 */
	public static double toDouble(Object object)
	{
		return Double.parseDouble(object.toString());
	}

	/**
	 * Converts <code>object</code> to a boolean.
	 * 
	 * @param object the object to convert.
	 * @return boolean
	 */
	public static boolean toBoolean(Object object)
	{
		if(object instanceof Boolean)
		{
			return (Boolean) object;
		}
		else if(object instanceof Integer)
		{
			if((Integer) object == 1) return true;
			else if((Integer) object == 0) return false;
		}
		return Boolean.parseBoolean(object.toString());
	}

	/**
	 * Generates a random string with a length of <code>length</code>.
	 * 
	 * @param length the length of the generated string.
	 * @return String
	 */
	public static String generateString(int length)
	{
		// Set allowed characters - Create new string to fill - Generate the string - Return string
		char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for(int i = 0; i < length; i++)
		{
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		String output = sb.toString();
		return output;
	}

	/**
	 * Generates a random integer with a length of <code>length</code>.
	 * 
	 * @param length the length of the generated integer.
	 * @return int
	 */
	public static int generateInt(int length)
	{
		// Set allowed characters - Create new string to fill - Generate the string - Return string
		char[] chars = "0123456789".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for(int i = 0; i < length; i++)
		{
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		int output = toInteger(sb.toString());
		return output;
	}

	/**
	 * Returns an integer between <code>min</code> and <code>max</code>.
	 * 
	 * @param min the minimum value of the returned integer.
	 * @param max the maximum value of the returned integer.
	 * @return int
	 */
	public static int generateIntRange(int min, int max)
	{
		return new Random().nextInt(max - min + 1) + min;
	}

	/**
	 * Returns a random boolean based on the <code>percent</code> passed in.
	 * 
	 * @param percent the percentage as a value from 1 - 100 (not 0 - 1).
	 * @return boolean
	 */
	public static boolean randomPercentBool(double percent)
	{
		Random rand = new Random();
		int chance = rand.nextInt((int) Math.ceil(1 / (percent / 100))) + 1;
		if(chance == 1) return true;
		return false;
	}
}
