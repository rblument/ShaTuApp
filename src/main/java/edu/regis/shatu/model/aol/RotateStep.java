/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.model.aol;

/**
 * Class models the Bit Rotation operation step that is done during hash computation. This class contains the rotation
 * direction (left or right), number of bits to rotate, number of bits in the string, the binary string, and the user
 * response.
 *
 * @author rickb
 */
public class RotateStep  {
    /**
     * Enum declaration for the rotation direction.
     * Note: SHA-256 only uses right rotation (ROTR)
     */
    public enum Direction {RIGHT}

    /**
     * Enum representing the direction of the rotation
     */
    private Direction direction;

    /**
     * The number of bits to rotate in the given direction
     */
    private int amount;

    /**
     * The length of the bit string to be rotated
     */
    private int length;

    /**
     * The binary string
     */
    private String data;

    /**
     * The user's response
     */
    private String userResponse;

    /**
     * Default constructor
     */
    public RotateStep() {
        
    }

    /**
     * Getter for the rotation direction
     *
     * @return Direction Enum for the rotation direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Setter for the rotation direction
     *
     * @param direction The direction to rotation the bits
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Getter for the number of bits to rotate
     *
     * @return the amount of bits to rotate
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Setter for the amount of bits to rotate the binary string by
     *
     * @param amount number of bits to rotate the binary string by
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Getter for the length of the binary string
     *
     * @return The length of the binary string
     */
    public int getLength() {
       return length;
    }

    /**
     * Setter for the length of the binary string
     *
     * @param length The length of the binary string
     */
    public void setLength(int length) {
       this.length = length;
    }

    /**
     * Getter for binary string to rotate
     *
     * @return The binary string that will be rotated
     */
    public String getData() {
        return data;
    }

    /**
     * Setter for the binary string that will be rotated
     *
     * @param data The binary string to be rotated
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Getter for the User's response to the rotation question
     *
     * @return The user's response to the question
     */
    public String getUserResponse(){
        return userResponse;
    }

    /**
     * Setter for the User's response to the question
     *
     * @param userResponse The user's response to the question
     */
    public void setUserResponse(String userResponse){
        this.userResponse = userResponse;
    }
}