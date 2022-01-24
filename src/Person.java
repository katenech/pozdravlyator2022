public class Person {
    private String name;
    private int day;
    private int month;

    public Person(String n, int d, int m){
      name=n;
      day=d;
      month=m;
    }

    public  String monthToText(int month){
        String textMonth="";
        switch (month){
            case 1:
                textMonth="января";
                break;
            case 2:
                textMonth="февраля";
                break;
            case 3:
                textMonth="марта";
                break;
            case 4:
                textMonth="апреля";
                break;
            case 5:
                textMonth="мая";
                break;
            case 6:
                textMonth="июня";
                break;
            case 7:
                textMonth="июля";
                break;
            case 8:
                textMonth="августа";
                break;
            case 9:
                textMonth="сентября";
                break;
            case 10:
                textMonth="октября";
                break;
            case 11:
                textMonth="ноября";
                break;
            case 12:
                textMonth="декабря";
                break;
        }
        return textMonth;
    }

    public String toString(){
        return name + " "+ day + " " + monthToText(month)+" "+month;
    }

    public String toStringShow(){
        return name + " "+ day + " " + monthToText(month);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
