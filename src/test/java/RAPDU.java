public class RAPDU {
    String rapdu;
    String resp;
    String sw;

    public void setRapdu(String rapdu) {
        this.rapdu = rapdu;
        this.rapdu = this.rapdu.strip();
        parseSW();
        parseResp();
    }

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public String getSw() {
        return sw;
    }

    public void setSw(String sw) {
        this.sw = sw;
    }

    void parseSW(){

        sw =  rapdu.substring(rapdu.length()-4);
    }
    void parseResp(){
        resp = rapdu.substring(0,rapdu.length()-4) ;
    }
}
