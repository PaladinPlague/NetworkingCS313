public class Checksum {

    byte[] dataBytes;

    /*
     * get data into Checksum
     */
    public Checksum(byte[] data) {
        this.dataBytes = data;
    }

    /*
     * This adds up all of the items in the list using the bitAddition helper function, once we get the total, it uses the
     * onesComplement helper function to translate the total into ones complement
     *
     */
    public String createCheckSum() {
        String answer = "0000000000000000";

        for (byte dataByte : this.dataBytes) {
            //convert each byte into binary
            String one = String.format("%16s", Integer.toBinaryString(dataByte & 255)).replace(' ', '0');

            //add two byte at a time
            answer = this.bitAddition(one, answer);

        }

        //having ana answer
        answer = this.onesComplement(answer);

        return answer;
    }
    /*
     * This function takes 2 words in binary format, and adds them together, we do this addition in binary format

     */
    public String bitAddition(String first, String second) {
        int c = 0;
        String answer = "";

        for(int i = first.length() - 1; i >= 0; --i) {
            int a = Integer.parseInt(Character.toString(first.charAt(i)));
            int b = Integer.parseInt(Character.toString(second.charAt(i)));
            int sum = a + b;
            if (c != 0) {
                sum += c;
            }

            if (sum == 2) {
                c = 1;
                answer = "0" + answer;
            } else if (sum == 3) {
                c = 1;
                answer = "1" + answer;
            } else if (sum == 0) {
                c = 0;
                answer = "0" + answer;
            } else if (sum == 1) {
                c = 0;
                answer = "1" + answer;
            }
        }

        String cString = String.format("%16s", Integer.toBinaryString(c & 255)).replace(' ', '0');
        if (cString.equals("0000000000000000")) {

            return answer;
        } else {

            return this.bitAddition(answer, cString);
        }
    }
    /*
     * This adds up all of the items in the list using the bitAddition helper function, we then return this total
     */
    public String createTotal() {
        String answer = "0000000000000000";

        for (byte dataByte : this.dataBytes) {
            String one = String.format("%16s", Integer.toBinaryString(dataByte & 255)).replace(' ', '0');
            answer = this.bitAddition(one, answer);

        }

        return answer;
    }
    /*
     * This takes a word in binary format and return the one's complement of that word
     */
    public String onesComplement(String change) {
        char[] binaryArray = change.toCharArray();

        for(int i = 0; i < binaryArray.length; ++i) {
            if (binaryArray[i] == '1') {
                binaryArray[i] = '0';
            } else {
                binaryArray[i] = '1';
            }
        }

        StringBuilder answer = new StringBuilder();
        answer.append(binaryArray);
        return answer.toString();
    }

}
