public class Checksum {

    byte[] dataBytes;

    public Checksum(byte[] data) {
        this.dataBytes = data;
    }

    public String createCheckSum() {

        String answer = "00000000";

        for(int i = 0; i<dataBytes.length; i++){

            String one = String.format("%8s", Integer.toBinaryString(dataBytes[i] & 0xFF)).replace(' ', '0');
            answer = bitAddition(one,answer);
        }

        return onesComplement(answer);
    }


    public String bitAddition(String first, String second){

        int c = 0;

        String answer = "";

        for(int i = first.length()-1; i>=0; i--) {

            int a = Integer.parseInt(Character.toString(first.charAt(i)));
            int b = Integer.parseInt(Character.toString(second.charAt(i)));

            int sum = a + b;
            //Adds the Carry
            if (c !=0) {
                sum = sum + c;
            }

            //Adds the string
            if (sum == 2) {
                c = 1;
                answer =  "0" + answer ;
            } else if (sum == 3) {
                c = 1;
                answer =  "1" + answer;
            } else if(sum == 0) {
                c = 0;
                answer =  "0" + answer;
            }
            else if(sum== 1){
                c = 0;
                answer = "1" + answer;
            }


        }

        String cString = String.format("%8s", Integer.toBinaryString(c & 0xFF)).replace(' ', '0');

        if(cString.equals("00000000")){
            return answer;
        }
        else {
            return bitAddition(answer,cString);
        }
    }


    public String createTotal() {

        String answer = "00000000";

        for(int i = 0; i<dataBytes.length; i++){
            String one = String.format("%8s", Integer.toBinaryString(dataBytes[i] & 0xFF)).replace(' ', '0');
            answer = bitAddition(one,answer);
        }

        return answer;
    }

    public String onesComplement(String change){

        char[] binaryArray = change.toCharArray();

        for(int i = 0; i<binaryArray.length; i++){

            if(binaryArray[i] == '1'){

                binaryArray[i] = '0';
            }
            else{
                binaryArray[i] = '1';
            }
        }
        StringBuilder answer = new StringBuilder();
        answer.append(binaryArray);
        return answer.toString();
    }

}
