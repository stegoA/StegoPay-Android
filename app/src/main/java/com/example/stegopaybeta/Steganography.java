package com.example.stegopaybeta;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.ArrayAdapter;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class Steganography {

    //Hashmap to store the all the 8, 3 bit combinations, and the pixel which contains each combination
    private HashMap<Integer, String> check_image_validity = new HashMap<>();

    //boolean array to check if all 8 combinations have been found or not.
    private Boolean[] values_done_in_hashmap = new Boolean[8];

    private boolean image_stego_pay_ready;

    private Bitmap image;

    public Steganography() {
        image_stego_pay_ready = false;
        //Filling the values_done_in_hashmap array with False values
        Arrays.fill(this.values_done_in_hashmap, Boolean.FALSE);
    }

    public Bitmap getImage() {
        return image;
    }

    public HashMap<Integer,String> getHashMap_1(){
        return check_image_validity;
    }

    public void setImage(Bitmap image) {
        this.image = image;
        Arrays.fill(this.values_done_in_hashmap, Boolean.FALSE);
        check_image_validity.clear();
        image_stego_pay_ready = false;
    }

    //Preprocessing to populate the hashmaps
    public void preProcessing() {

        //Get width and height of the image
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int imageSize = width * height;

        int[] pixelsInt = new int[imageSize];
        image.getPixels(pixelsInt, 0, image.getWidth(), 0, 0, width, height);

        // System.out.println("Int array length: " + pixelsInt.length);

        //Check if image has a Alpha channel, or does it only have red green and blue channels
        final boolean hasAlphaChannel = image.hasAlpha();

        int red = 0;
        int green = 0;
        int blue = 0;

        int pixels_mapped = 0;


        //Go through the Pixels byte array
        for (int pixel = 0, row = 0, col = 0; pixel < pixelsInt.length; pixel++) {

            //Set to store unique values
            //to store all the 3 bit combinations of each pixel
            Set<Integer> possible_three_bit_combinations = new HashSet<>();
            //If the image has a alpha channel
            if (hasAlphaChannel) {
                //Get the alpha value which is the first value in the pixels byte array
                //&0xff to convert byte value to int, since byte is signed, and we require unsigned value from 0 to 255.
                int alpha = (pixelsInt[pixel] >> 24) & 0xff;
                //Get the red value which is the fourth value in the pixels byte array, so the pixels byte array stores
                red = (pixelsInt[pixel] >> 16) & 0xff;
                //Get the green value which is the third value in the pixels byte array
                green = (pixelsInt[pixel] >> 8) & 0xff;
                //Get the blue value which is the second value in the pixels byte array
                blue = (pixelsInt[pixel]) & 0xff;

            } //If the image doesnt have a alpha channel
            else {
                //Get the blue, green and red values in order
                //Bitwise & with 0xff to convert byte to unsigned integer, to get a value between 0 and 255.
                red = (pixelsInt[pixel] >> 16) & 0xff;
                green = (pixelsInt[pixel] >> 8) & 0xff;
                blue = (pixelsInt[pixel]) & 0xff;
            }

            //Using the formulas to compute all the 3 bit combinations of a pixel
            possible_three_bit_combinations.add((int) ((Math.floor(blue / 32)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(blue / 16)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(blue / 8)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(blue / 4)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(blue / 2)) % 8));
            possible_three_bit_combinations.add(blue % 8);

            possible_three_bit_combinations.add((int) ((Math.floor(green / 32)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(green / 16)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(green / 8)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(green / 4)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(green / 2)) % 8));
            possible_three_bit_combinations.add(green % 8);

            possible_three_bit_combinations.add((int) ((Math.floor(red / 32)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(red / 16)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(red / 8)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(red / 4)) % 8));
            possible_three_bit_combinations.add((int) ((Math.floor(red / 2)) % 8));
            possible_three_bit_combinations.add(red % 8);

            //Pixel location (row X col)
            String pixelLocation = row + "X" + col;

            //Going through the set
            for (int temp : possible_three_bit_combinations) {

                //If this value hasn't been found in other pixels
                if (values_done_in_hashmap[temp] == false) {

                    //Insert into the hashmap the value (0 to 7) and the pixel location
                    check_image_validity.put(temp, pixelLocation);

                    //Set boolean value for this key to be true.
                    values_done_in_hashmap[temp] = true;
                    pixels_mapped++;
                }

            }
            if (pixels_mapped == 8) {
                break;
            }

            //Increment to go to the next pixel which is located 3 pixels beyond, and increment column to store the value in the corresponding 2D array
            col++;
            //If the width has been reached increment the row.
            if (col == width) {
                col = 0;
                row++;
            }
        }
        System.out.println(check_image_validity);
    }

    //Updated preprocessing without sets
    public void preProcessing2(){
        //Get width and height of the image
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int imageSize = width * height;

        int[] pixelsInt = new int[imageSize];
        image.getPixels(pixelsInt, 0, image.getWidth(), 0, 0, width, height);

        // System.out.println("Int array length: " + pixelsInt.length);

        //Check if image has a Alpha channel, or does it only have red green and blue channels
        final boolean hasAlphaChannel = image.hasAlpha();


        int pixels_mapped = 0;


        //Go through the Pixels byte array
        for (int pixel = 0, row = 0, col = 0; pixel < pixelsInt.length; pixel++) {

            int red;
            int green;
            int blue;

            //If the image has a alpha channel
            if (hasAlphaChannel) {
                //Get the alpha value which is the first value in the pixels byte array
                //&0xff to convert byte value to int, since byte is signed, and we require unsigned value from 0 to 255.
                int alpha = (pixelsInt[pixel] >> 24) & 0xff;
                //Get the red value which is the fourth value in the pixels byte array, so the pixels byte array stores
                red = (pixelsInt[pixel] >> 16) & 0xff;
                //Get the green value which is the third value in the pixels byte array
                green = (pixelsInt[pixel] >> 8) & 0xff;
                //Get the blue value which is the second value in the pixels byte array
                blue = (pixelsInt[pixel]) & 0xff;

            } //If the image doesnt have a alpha channel
            else {
                //Get the blue, green and red values in order
                //Bitwise & with 0xff to convert byte to unsigned integer, to get a value between 0 and 255.
                red = (pixelsInt[pixel] >> 16) & 0xff;
                green = (pixelsInt[pixel] >> 8) & 0xff;
                blue = (pixelsInt[pixel]) & 0xff;
            }
            int formula_result = 0;

            //Pixel location (row X col)
            String pixelLocation = row + "X" + col;

            //Using the formulas to compute all the 3 bit combinations of a pixel
            formula_result = (int) ((Math.floor(blue / 32)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(blue / 16)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(blue / 8)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(blue / 4)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(blue / 2)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (blue % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(green / 32)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(green / 16)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(green / 8)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(green / 4)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(green / 2)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (green % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }


            formula_result = (int) ((Math.floor(red / 32)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(red / 16)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(red / 8)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(red / 4)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (int) ((Math.floor(red / 2)) % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            formula_result = (red % 8);
            if (values_done_in_hashmap[formula_result] == false) {
                update_for_preprocessing(formula_result, pixelLocation);
                pixels_mapped++;
            }

            if (pixels_mapped == 8) {
                break;
            }

            //Increment to go to the next pixel which is located 3 pixels beyond, and increment column to store the value in the corresponding 2D array
            col++;
            //If the width has been reached increment the row.
            if (col == width) {
                col = 0;
                row++;
            }
        }
        System.out.println(check_image_validity);
    }

    public void update_for_preprocessing(int key, String pixelLocation) {
        //Setting hashmap_1
        check_image_validity.put(key, pixelLocation);

        //Set boolean value for this key to be true.
        values_done_in_hashmap[key] = true;
    }

    public String single_pattern_mapping(String CCDetailsBinary) {

        //Total number of bits mapped
        int number_of_bits_done = 0;

        //Total number of bits to be mapped, which is equal to the length of the secret message(Credit Card details)
        int total_number_of_bits_to_map = CCDetailsBinary.length();

        //To store the mapping key
        String mappingKey = "";

        //Temp hashmap, to store the pixel location and its RGB value
        //So that the image is not visited every time to get a pixel
        HashMap<String, String> tempHashMap_2 = new HashMap<String, String>();

        //While all the bits are not mapped
        while (number_of_bits_done != total_number_of_bits_to_map) {

            //Minimum number of bits to map in current round
            int bits_to_map;

            //If there are 2 bits remaining in the secret message, bits to map is set to 2
            if ((total_number_of_bits_to_map - number_of_bits_done) == 2) {
                bits_to_map = 2;
            }
            //Else if there is 1 bit remaining in the secret message, bits to map is set to 1
            else if ((total_number_of_bits_to_map - number_of_bits_done) == 1) {
                bits_to_map = 1;
            }
            //Else at minimum three bits will be mapped to a pixel
            else {
                bits_to_map = 3;
            }

            //Taking the next (minimum number of bits) bits to map from the secret message (Credit card details)
            String secretMessagePattern = CCDetailsBinary.substring(number_of_bits_done, number_of_bits_done + bits_to_map);

            //Converting those secretMessagePattern to decimal.
            int secretMessagePattern_Decimal = Integer.parseInt(secretMessagePattern, 2);

            //Get the appropriate pixel location from the hashmap which stores the 8 three bit combinations, with the pixel location
            String pixelLocation_to_match_in = check_image_validity.get(secretMessagePattern_Decimal);

            String pixel_to_match_in = "";
            if(!(tempHashMap_2.containsKey(pixelLocation_to_match_in))) {
                //Splitting the pixel location by delimitier X to get row and column of the pixel
                String[] pixels = pixelLocation_to_match_in.split("X");

                //Row and column pixels of the pixel location
                int rowPixel = Integer.parseInt(pixels[0]);
                int colPixel = Integer.parseInt(pixels[1]);

                //Getting the pixel at the location
                int colour = image.getPixel(colPixel, rowPixel);
                int red = Color.red(colour);
                int blue = Color.blue(colour);
                int green = Color.green(colour);

                //Converting the pixel to binary
                String redBinary = Integer.toBinaryString(red);
                redBinary = String.format("%8s", redBinary).replace(' ', '0');

                String greenBinary = Integer.toBinaryString(green);
                greenBinary = String.format("%8s", greenBinary).replace(' ', '0');

                String blueBinary = Integer.toBinaryString(blue);
                blueBinary = String.format("%8s", blueBinary).replace(' ', '0');

                //Pixel to match in is red binary + green binary + blue binary
                pixel_to_match_in = redBinary + greenBinary + blueBinary;

                tempHashMap_2.put(pixelLocation_to_match_in,pixel_to_match_in);
            }
            else{
                pixel_to_match_in = tempHashMap_2.get(pixelLocation_to_match_in);
            }

            //Setting counter to go through the pixel
            int i = 0;

            //Going through the 24 pixel bits
            while (i < pixel_to_match_in.length()) {

                //System.out.println("I : " + i);
                //To store the number of bits done in current round, for forming the mapping key
                int number_of_bits_done_in_current_round = 0;

                //If the number of bits to map from the ith location exceeds the pixel to match in length then break
                if ((i + bits_to_map) > pixel_to_match_in.length()) {
                    // System.out.println("In first if after while which will break");
                    break;
                }

                //Taking the minimum number of bits to map from the pixel
                String to_match_in = pixel_to_match_in.substring(i, i + bits_to_map);
                //System.out.println("To match in : " + to_match_in);

                //Converting to_match_in to decimal
                int to_match_in_decimal = Integer.parseInt(to_match_in, 2);
                //System.out.println("To match in decimal : " + to_match_in_decimal);

                //Checking if the minimum number of bits to map matches
                if (secretMessagePattern_Decimal == to_match_in_decimal) {

                    //System.out.println("Match found");
                    //Incrementing number of bits done in current round to minimum number of bits to match
                    number_of_bits_done_in_current_round += bits_to_map;

                    //Incrementing total number of bits done
                    number_of_bits_done += number_of_bits_done_in_current_round;

                    //System.out.println("Number of bits done : " + number_of_bits_done);
                    //If all the bits are not yet done
                    if (number_of_bits_done < total_number_of_bits_to_map) {

                        //System.out.println("Trying to find further");
                        //Starting after the bits mapped already, to the pixel length
                        for (int j = i + bits_to_map; j < pixel_to_match_in.length(); j++) {
                            //System.out.println("In Loop to find further : i " + i + " j : " + j);
                            //System.out.println("CCDetailsBinary.charArt(number_of_bits_done): " + CCDetailsBinary.charAt(number_of_bits_done));
                            //System.out.println("pixel_to_match_in.charAt(j) : " + pixel_to_match_in.charAt(j));

                            //If the next bit in secret message is equal to the next bit in pixel to match in
                            if (CCDetailsBinary.charAt(number_of_bits_done) == pixel_to_match_in.charAt(j)) {

                                //Increment number of bits done in current round
                                number_of_bits_done_in_current_round += 1;

                                //Increment total number of bits done
                                number_of_bits_done += 1;
                                //System.out.println("Another find increment by 1 : number_of_bits_done : " + number_of_bits_done);
                            } //Once a match is not found then break
                            else {
                                //System.out.println("no match found breaking for loop");
                                break;
                            }
                            //Once all the bits are found then break
                            if (number_of_bits_done >= total_number_of_bits_to_map) {
                                //System.out.println("Breaking, already all of them are found");
                                break;
                            }

                        }
                    }

                    //Forming mapping key after end of round and break from the while loop
                    //System.out.println("Forming mapping key ");
                    mappingKey += number_of_bits_done_in_current_round + ":" + i + ":" + pixelLocation_to_match_in + ";";
                    //System.out.println("Mapping key : " + mappingKey);
                    break;
                } //Else if the minimum number of bits to map dont match
                else {
                    //System.out.println("In else ");
                    //Get shift of the secret message using the shift table
                    int shiftAmount = getShift(secretMessagePattern_Decimal, to_match_in_decimal);
                    //System.out.println("Shift amount : " + shiftAmount);
                    //Increment i by the shift amoung
                    i = i + shiftAmount;
                    //System.out.println("New I : " + i);
                }
                //Else if all the bits are mapped then break also
                if (number_of_bits_done >= total_number_of_bits_to_map) {
                    //System.out.println("LAST BREAKING");
                    break;
                }

            }

        }
        return mappingKey;
    }

    public String single_pattern_mapping(String CCDetailsBinary, Bitmap Image, HashMap<Integer,String> hashMap_1){

        //Total number of bits mapped
        int number_of_bits_done = 0;

        //Total number of bits to be mapped, which is equal to the length of the secret message(Credit Card details)
        int total_number_of_bits_to_map = CCDetailsBinary.length();

        //To store the mapping key
        String mappingKey = "";

        //Temp hashmap, to store the pixel location and its RGB value
        //So that the image is not visited every time to get a pixel
        HashMap<String, String> tempHashMap_2 = new HashMap<String, String>();

        //While all the bits are not mapped
        while (number_of_bits_done != total_number_of_bits_to_map) {

            //Minimum number of bits to map in current round
            int bits_to_map;

            //If there are 2 bits remaining in the secret message, bits to map is set to 2
            if ((total_number_of_bits_to_map - number_of_bits_done) == 2) {
                bits_to_map = 2;
                //System.out.println("First If Bits to map");
            } //Else if there is 1 bit remaining in the secret message, bits to map is set to 1
            else if ((total_number_of_bits_to_map - number_of_bits_done) == 1) {
                bits_to_map = 1;
                //System.out.println("Else if Bits to map");
            } //Else at minimum three bits will be mapped to a pixel
            else {
                bits_to_map = 3;
                //System.out.println("Else bits to map");
            }

            //System.out.println("Bits to map : " + bits_to_map);
            //Taking the next (minimum number of bits) bits to map from the secret message (Credit card details)
            String secretMessagePattern = CCDetailsBinary.substring(number_of_bits_done, number_of_bits_done + bits_to_map);
            //System.out.println("Secret message pattern : " + secretMessagePattern);

            //Converting those secretMessagePattern to decimal.
            int secretMessagePattern_Decimal = Integer.parseInt(secretMessagePattern, 2);
            //System.out.println("Secret message pattern decimal : " + secretMessagePattern_Decimal);

            //Get the appropriate pixel location from the hashmap which stores the 8 three bit combinations, with the pixel location
            String pixelLocation_to_match_in = hashMap_1.get(secretMessagePattern_Decimal);

            String pixel_to_match_in = "";

            if(!(tempHashMap_2.containsKey(pixelLocation_to_match_in))) {
                //Splitting the pixel location by delimitier X to get row and column of the pixel
                String[] pixels = pixelLocation_to_match_in.split("X");

                //Row and column pixels of the pixel location
                int rowPixel = Integer.parseInt(pixels[0]);
                int colPixel = Integer.parseInt(pixels[1]);

                //Getting the pixel at the location
                int colour = Image.getPixel(colPixel, rowPixel);
                int red = Color.red(colour);
                int blue = Color.blue(colour);
                int green = Color.green(colour);

                //Converting the pixel to binary
                String redBinary = Integer.toBinaryString(red);
                redBinary = String.format("%8s", redBinary).replace(' ', '0');

                String greenBinary = Integer.toBinaryString(green);
                greenBinary = String.format("%8s", greenBinary).replace(' ', '0');

                String blueBinary = Integer.toBinaryString(blue);
                blueBinary = String.format("%8s", blueBinary).replace(' ', '0');

                //Pixel to match in is red binary + green binary + blue binary
                pixel_to_match_in = redBinary + greenBinary + blueBinary;

                tempHashMap_2.put(pixelLocation_to_match_in,pixel_to_match_in);
            }
            else{
                pixel_to_match_in = tempHashMap_2.get(pixelLocation_to_match_in);
            }

            //Setting counter to go through the pixel
            int i = 0;

            //Going through the 24 pixel bits
            while (i < pixel_to_match_in.length()) {

                //System.out.println("I : " + i);
                //To store the number of bits done in current round, for forming the mapping key
                int number_of_bits_done_in_current_round = 0;

                //If the number of bits to map from the ith location exceeds the pixel to match in length then break
                if ((i + bits_to_map) > pixel_to_match_in.length()) {
                    // System.out.println("In first if after while which will break");
                    break;
                }

                //Taking the minimum number of bits to map from the pixel
                String to_match_in = pixel_to_match_in.substring(i, i + bits_to_map);
                //System.out.println("To match in : " + to_match_in);

                //Converting to_match_in to decimal
                int to_match_in_decimal = Integer.parseInt(to_match_in, 2);
                //System.out.println("To match in decimal : " + to_match_in_decimal);

                //Checking if the minimum number of bits to map matches
                if (secretMessagePattern_Decimal == to_match_in_decimal) {

                    //System.out.println("Match found");
                    //Incrementing number of bits done in current round to minimum number of bits to match
                    number_of_bits_done_in_current_round += bits_to_map;

                    //Incrementing total number of bits done
                    number_of_bits_done += number_of_bits_done_in_current_round;

                    //System.out.println("Number of bits done : " + number_of_bits_done);
                    //If all the bits are not yet done
                    if (number_of_bits_done < total_number_of_bits_to_map) {

                        //System.out.println("Trying to find further");
                        //Starting after the bits mapped already, to the pixel length
                        for (int j = i + bits_to_map; j < pixel_to_match_in.length(); j++) {
                            //System.out.println("In Loop to find further : i " + i + " j : " + j);
                            //System.out.println("CCDetailsBinary.charArt(number_of_bits_done): " + CCDetailsBinary.charAt(number_of_bits_done));
                            //System.out.println("pixel_to_match_in.charAt(j) : " + pixel_to_match_in.charAt(j));

                            //If the next bit in secret message is equal to the next bit in pixel to match in
                            if (CCDetailsBinary.charAt(number_of_bits_done) == pixel_to_match_in.charAt(j)) {

                                //Increment number of bits done in current round
                                number_of_bits_done_in_current_round += 1;

                                //Increment total number of bits done
                                number_of_bits_done += 1;
                                //System.out.println("Another find increment by 1 : number_of_bits_done : " + number_of_bits_done);
                            } //Once a match is not found then break
                            else {
                                //System.out.println("no match found breaking for loop");
                                break;
                            }
                            //Once all the bits are found then break
                            if (number_of_bits_done >= total_number_of_bits_to_map) {
                                //System.out.println("Breaking, already all of them are found");
                                break;
                            }

                        }
                    }

                    //Forming mapping key after end of round and break from the while loop
                    //System.out.println("Forming mapping key ");
                    mappingKey += number_of_bits_done_in_current_round + ":" + i + ":" + pixelLocation_to_match_in + ";";
                    //System.out.println("Mapping key : " + mappingKey);
                    break;
                } //Else if the minimum number of bits to map dont match
                else {
                    //System.out.println("In else ");
                    //Get shift of the secret message using the shift table
                    int shiftAmount = getShift(secretMessagePattern_Decimal, to_match_in_decimal);
                    //System.out.println("Shift amount : " + shiftAmount);
                    //Increment i by the shift amoung
                    i = i + shiftAmount;
                    //System.out.println("New I : " + i);
                }
                //Else if all the bits are mapped then break also
                if (number_of_bits_done >= total_number_of_bits_to_map) {
                    //System.out.println("LAST BREAKING");
                    break;
                }

            }

        }
        return mappingKey;
    }

    public int getShift(int patternDecimal, int textDecimal) {
        int shift = 0;
        if (patternDecimal == 0) {
            switch (textDecimal) {
                case 1:
                    shift = 3;
                    break;
                case 2:
                    shift = 2;
                    break;
                case 3:
                    shift = 3;
                    break;
                case 4:
                    shift = 1;
                    break;
                case 5:
                    shift = 3;
                    break;
                case 6:
                    shift = 2;
                    break;
                case 7:
                    shift = 3;
                    break;
            }
        } else if (patternDecimal == 1) {
            switch (textDecimal) {
                case 0:
                    shift = 1;
                    break;
                case 2:
                    shift = 2;
                    break;
                case 3:
                    shift = 3;
                    break;
                case 4:
                    shift = 1;
                    break;
                case 5:
                    shift = 3;
                    break;
                case 6:
                    shift = 2;
                    break;
                case 7:
                    shift = 3;
                    break;
            }
        } else if (patternDecimal == 2) {
            switch (textDecimal) {
                case 0:
                    shift = 2;
                    break;
                case 1:
                    shift = 1;
                    break;
                case 3:
                    shift = 3;
                    break;
                case 4:
                    shift = 2;
                    break;
                case 5:
                    shift = 1;
                    break;
                case 6:
                    shift = 2;
                    break;
                case 7:
                    shift = 3;
                    break;
            }
        } else if (patternDecimal == 3) {
            switch (textDecimal) {
                case 0:
                    shift = 2;
                    break;
                case 1:
                    shift = 1;
                    break;
                case 2:
                    shift = 2;
                    break;
                case 4:
                    shift = 2;
                    break;
                case 5:
                    shift = 1;
                    break;
                case 6:
                    shift = 2;
                    break;
                case 7:
                    shift = 3;
                    break;
            }
        } else if (patternDecimal == 4) {
            switch (textDecimal) {
                case 0:
                    shift = 3;
                    break;
                case 1:
                    shift = 2;
                    break;
                case 2:
                    shift = 1;
                    break;
                case 3:
                    shift = 2;
                    break;
                case 5:
                    shift = 2;
                    break;
                case 6:
                    shift = 1;
                    break;
                case 7:
                    shift = 2;
                    break;
            }
        } else if (patternDecimal == 5) {
            switch (textDecimal) {
                case 0:
                    shift = 3;
                    break;
                case 1:
                    shift = 2;
                    break;
                case 2:
                    shift = 1;
                    break;
                case 3:
                    shift = 2;
                    break;
                case 4:
                    shift = 3;
                    break;
                case 6:
                    shift = 1;
                    break;
                case 7:
                    shift = 2;
                    break;
            }
        } else if (patternDecimal == 6) {
            switch (textDecimal) {
                case 0:
                    shift = 3;
                    break;
                case 1:
                    shift = 2;
                    break;
                case 2:
                    shift = 3;
                    break;
                case 3:
                    shift = 1;
                    break;
                case 4:
                    shift = 3;
                    break;
                case 5:
                    shift = 2;
                    break;
                case 7:
                    shift = 1;
                    break;
            }
        } else if (patternDecimal == 7) {
            switch (textDecimal) {
                case 0:
                    shift = 3;
                    break;
                case 1:
                    shift = 2;
                    break;
                case 2:
                    shift = 3;
                    break;
                case 3:
                    shift = 1;
                    break;
                case 4:
                    shift = 3;
                    break;
                case 5:
                    shift = 2;
                    break;
                case 6:
                    shift = 3;
                    break;
            }
        }
        return shift;
    }

    public boolean check_image_validity() {
        if (check_image_validity.size() >= 8) {
            image_stego_pay_ready = true;
        } else {
            image_stego_pay_ready = false;
        }
        return image_stego_pay_ready;
    }

    public String decoding(String mappingKey, Bitmap Image) {

        //Getting each mapping by splitting using the semi colon delimiter
        String[] mappings = mappingKey.split(";");

        //Temp hashmap to store pixel location as key , and its RGB values in binary
        //to make sure that the image is not visited every time to get the RGB values in binary.
        HashMap<String, String> tempHashMap_2 = new HashMap<>();

        //To store the decoded binary text
        String decodingBinary = "";

        //Going through each mapping
        for (int i = 0; i < mappings.length; i++) {
            //System.out.println("Mapping : " + i + " : " + mappings[i]);

            //Splitting the contents of each mapping
            String[] singleMapping = mappings[i].split(":");

            //Splitting the pixel location by delimitier X to get row and column of the pixel
            String[] pixels = singleMapping[2].split("X");

            //Starting from value is the 2nd mapping key component
            int startingFrom = Integer.parseInt(singleMapping[1]);

            //Number of bits done in a round is the 1st mapping key component
            int numberOfBits = Integer.parseInt(singleMapping[0]);

            String pixel = "";

            //Check if pixel is not in temp hashmap
            if(!(tempHashMap_2.containsKey(singleMapping[2]))) {
                //Row and column pixels of the pixel location
                int rowPixel = Integer.parseInt(pixels[0]);
                int colPixel = Integer.parseInt(pixels[1]);

                //Get RGB
                int colour = Image.getPixel(colPixel, rowPixel);
                int red = Color.red(colour);
                int blue = Color.blue(colour);
                int green = Color.green(colour);

                //Convert RGB values to binary
                String redBinary = Integer.toBinaryString(red);
                redBinary = String.format("%8s", redBinary).replace(' ', '0');

                String greenBinary = Integer.toBinaryString(green);
                greenBinary = String.format("%8s", greenBinary).replace(' ', '0');

                String blueBinary = Integer.toBinaryString(blue);
                blueBinary = String.format("%8s", blueBinary).replace(' ', '0');

                pixel = redBinary + greenBinary + blueBinary;

                //add pixel to temp hashmap 2
                tempHashMap_2.put(singleMapping[2], pixel);
            }
            else{
                pixel = tempHashMap_2.get(singleMapping[2]);
            }

            //System.out.println("Pixel : " + pixel);
            decodingBinary += pixel.substring(startingFrom, startingFrom + numberOfBits);
        }

        return decodingBinary;
    }

    //OLD DECODING
    /*
    public String decoding(String mappingKey, Bitmap image) {

        //Getting each mapping by splitting using the semi colon delimiter
        String[] mappings = mappingKey.split(";");

        //To store the decoded binary text
        String decodingBinary = "";

        //Going through each mapping
        for (int i = 0; i < mappings.length; i++) {
            //System.out.println("Mapping : " + i + " : " + mappings[i]);

            //Splitting the contents of each mapping
            String[] singleMapping = mappings[i].split(":");

            //Splitting the pixel location by delimitier X to get row and column of the pixel
            String[] pixels = singleMapping[2].split("X");

            //Starting from value is the 2nd mapping key component
            int startingFrom = Integer.parseInt(singleMapping[1]);

            //Number of bits done in a round is the 1st mapping key component
            int numberOfBits = Integer.parseInt(singleMapping[0]);

            //Row and column pixels of the pixel location
            int rowPixel = Integer.parseInt(pixels[0]);
            int colPixel = Integer.parseInt(pixels[1]);

            int colour = image.getPixel(colPixel, rowPixel);
            int red = Color.red(colour);
            int blue = Color.blue(colour);
            int green = Color.green(colour);

            String redBinary = Integer.toBinaryString(red);
            redBinary = String.format("%8s", redBinary).replace(' ', '0');

            String greenBinary = Integer.toBinaryString(green);
            greenBinary = String.format("%8s", greenBinary).replace(' ', '0');

            String blueBinary = Integer.toBinaryString(blue);
            blueBinary = String.format("%8s", blueBinary).replace(' ', '0');

            String pixel = redBinary + greenBinary + blueBinary;

            //System.out.println("Pixel : " + pixel);
            decodingBinary += pixel.substring(startingFrom, startingFrom + numberOfBits);
        }

        return decodingBinary;
    }*/
    /*OLD SINGLE PATTERN MAPPING
    public String single_pattern_mapping(String CCDetailsBinary) {

        //Total number of bits mapped
        int number_of_bits_done = 0;

        //Total number of bits to be mapped, which is equal to the length of the secret message(Credit Card details)
        int total_number_of_bits_to_map = CCDetailsBinary.length();

        //To store the mapping key
        String mappingKey = "";

        //While all the bits are not mapped
        while (number_of_bits_done != total_number_of_bits_to_map) {

            //Minimum number of bits to map in current round
            int bits_to_map;

            //If there are 2 bits remaining in the secret message, bits to map is set to 2
            if ((total_number_of_bits_to_map - number_of_bits_done) == 2) {
                bits_to_map = 2;
                //System.out.println("First If Bits to map");
            } //Else if there is 1 bit remaining in the secret message, bits to map is set to 1
            else if ((total_number_of_bits_to_map - number_of_bits_done) == 1) {
                bits_to_map = 1;
                //System.out.println("Else if Bits to map");
            } //Else at minimum three bits will be mapped to a pixel
            else {
                bits_to_map = 3;
                //System.out.println("Else bits to map");
            }

            //System.out.println("Bits to map : " + bits_to_map);
            //Taking the next (minimum number of bits) bits to map from the secret message (Credit card details)
            String secretMessagePattern = CCDetailsBinary.substring(number_of_bits_done, number_of_bits_done + bits_to_map);
            //System.out.println("Secret message pattern : " + secretMessagePattern);

            //Converting those secretMessagePattern to decimal.
            int secretMessagePattern_Decimal = Integer.parseInt(secretMessagePattern, 2);
            //System.out.println("Secret message pattern decimal : " + secretMessagePattern_Decimal);

            //Get the appropriate pixel location from the hashmap which stores the 8 three bit combinations, with the pixel location
            String pixelLocation_to_match_in = check_image_validity.get(secretMessagePattern_Decimal);

            //Get the pixel in binary from the valid pixels in binary hashmap
            String pixel_to_match_in = valid_pixels_in_binary.get(pixelLocation_to_match_in);
            //System.out.println("Pixel to match in : " + pixel_to_match_in);

            //Setting counter to go through the pixel
            int i = 0;

            //Going through the 24 pixel bits
            while (i < pixel_to_match_in.length()) {

                //System.out.println("I : " + i);
                //To store the number of bits done in current round, for forming the mapping key
                int number_of_bits_done_in_current_round = 0;

                //If the number of bits to map from the ith location exceeds the pixel to match in length then break
                if ((i + bits_to_map) > pixel_to_match_in.length()) {
                    // System.out.println("In first if after while which will break");
                    break;
                }

                //Taking the minimum number of bits to map from the pixel
                String to_match_in = pixel_to_match_in.substring(i, i + bits_to_map);
                //System.out.println("To match in : " + to_match_in);

                //Converting to_match_in to decimal
                int to_match_in_decimal = Integer.parseInt(to_match_in, 2);
                //System.out.println("To match in decimal : " + to_match_in_decimal);

                //Checking if the minimum number of bits to map matches
                if (secretMessagePattern_Decimal == to_match_in_decimal) {

                    //System.out.println("Match found");
                    //Incrementing number of bits done in current round to minimum number of bits to match
                    number_of_bits_done_in_current_round += bits_to_map;

                    //Incrementing total number of bits done
                    number_of_bits_done += number_of_bits_done_in_current_round;

                    //System.out.println("Number of bits done : " + number_of_bits_done);
                    //If all the bits are not yet done
                    if (number_of_bits_done < total_number_of_bits_to_map) {

                        //System.out.println("Trying to find further");
                        //Starting after the bits mapped already, to the pixel length
                        for (int j = i + bits_to_map; j < pixel_to_match_in.length(); j++) {
                            //System.out.println("In Loop to find further : i " + i + " j : " + j);
                            //System.out.println("CCDetailsBinary.charArt(number_of_bits_done): " + CCDetailsBinary.charAt(number_of_bits_done));
                            //System.out.println("pixel_to_match_in.charAt(j) : " + pixel_to_match_in.charAt(j));

                            //If the next bit in secret message is equal to the next bit in pixel to match in
                            if (CCDetailsBinary.charAt(number_of_bits_done) == pixel_to_match_in.charAt(j)) {

                                //Increment number of bits done in current round
                                number_of_bits_done_in_current_round += 1;

                                //Increment total number of bits done
                                number_of_bits_done += 1;
                                //System.out.println("Another find increment by 1 : number_of_bits_done : " + number_of_bits_done);
                            } //Once a match is not found then break
                            else {
                                //System.out.println("no match found breaking for loop");
                                break;
                            }
                            //Once all the bits are found then break
                            if (number_of_bits_done >= total_number_of_bits_to_map) {
                                //System.out.println("Breaking, already all of them are found");
                                break;
                            }

                        }
                    }

                    //Forming mapping key after end of round and break from the while loop
                    //System.out.println("Forming mapping key ");
                    mappingKey += number_of_bits_done_in_current_round + ":" + i + ":" + pixelLocation_to_match_in + ";";
                    //System.out.println("Mapping key : " + mappingKey);
                    break;
                } //Else if the minimum number of bits to map dont match
                else {
                    //System.out.println("In else ");
                    //Get shift of the secret message using the shift table
                    int shiftAmount = getShift(secretMessagePattern_Decimal, to_match_in_decimal);
                    //System.out.println("Shift amount : " + shiftAmount);
                    //Increment i by the shift amoung
                    i = i + shiftAmount;
                    //System.out.println("New I : " + i);
                }
                //Else if all the bits are mapped then break also
                if (number_of_bits_done >= total_number_of_bits_to_map) {
                    //System.out.println("LAST BREAKING");
                    break;
                }

            }

        }
        return mappingKey;
    }*/

}



