import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**Создать приложение "Поздравлятор". Функциональность приложения - ведение списка дней рождения
(далее ДР) друзей/знакомых/сотрудников, а именно:
    • Отображение всего списка ДР (дополнительные возможности, такие как сортировка, выделение текущих
    и просроченных и т.п. - остаются на усмотрение реализующего)
    • Отображение списка сегодняшних и ближайших ДР (дополнительные возможности, такие как сортировка,
    выделение текущих и просроченных и т.п. - остаются на усмотрение реализующего)
    • Добавление записей в список ДР
    • Удаление записей из списка ДР
    • Редактирование записей в списке ДР
Уровень 1: Консольное Java-приложение, информация хранится в объектах в памяти,
есть возможность работы с файлами для сохранения/загрузки данных (для обеспечения персистентности объектов).
Управление через текстовое меню, по умолчанию при запуске программа выдает список сегодняшних и ближайших ДР.
 * @author Kate Glech katenech@gmail.com
 */
public class Main {
    List<Person> bdList;
    Scanner sc;
    BufferedReader reader;
    boolean isEnd;
    List<String > list;

    String bdPath = "bdList.txt";
    File bdFile;

    String name;
    StringBuffer sb;
    int month, day;

    Calendar calendar;
    Locale locale;

    int dayNow, monthNow;

    public static void main(String[] args) {
        new Main();
    }

    public Main(){
        locale = new Locale("ru");
        calendar = Calendar.getInstance(locale);

        reader = new BufferedReader(new InputStreamReader(System.in));
        sc = new Scanner(System.in);
        bdList = new ArrayList();
        bdFile = new File(bdPath);
        if(bdFile.exists())
            readDatabase();
        else {
            Person person = new Person("Я", 26, 6);
            bdList.add(person);
        }

        getTodayBirth();

        while (!isEnd)
            showMenu();
    }

    ////////////////////////////////////////////////////////////////////////////
    //сегодняшние именинники:
    private void getTodayBirth(){
        dayNow = calendar.get(Calendar.DATE);
        monthNow = calendar.get(Calendar.MONTH)+1;

        System.out.println("Сегодня "+dayNow+" "+calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale));
        System.out.println("Не забыть поздравить:");
        for(Person p : bdList){
            if(dayNow==p.getDay() && monthNow==p.getMonth())
                System.out.println(p.toStringShow());
        }
        System.out.println();
    }

    /////////////////////////////////////////////////////////////////////////
    //меню программы
    private void showMenu() {
        int choise = 0;
        System.out.println("Меню \"Поздравлятор\"");
        System.out.println("1 - посмотреть список");
        System.out.println("2 - поиск в списке");
        System.out.println("3 - добавить запись");
        System.out.println("4 - удалить запись");
        System.out.println("5 - редактировать запись");
        System.out.println("6 - выход");
        System.out.println();

        try {
            choise = Integer.parseInt(sc.nextLine());
        }
        catch (NumberFormatException e){
            System.out.println("ошибка меню");

        }

        switch (choise){
            case 1:
                showList();
                break;
            case 2:
                find();
                break;
            case 3:
                addPerson();
                break;
            case 4:
                delPerson();
                break;
            case 5:
                correctPerson();
                break;
            case 6:
                System.out.println("Завершение работы");
                isEnd=true;
                break;
            default:
                System.out.println("Попробуйте еще раз");
                showMenu();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    //1 - посмотреть список
    private void showList(){
        Comparator<Person> comparator = Comparator.comparing(person -> person.getMonth());
        comparator = comparator.thenComparing(Comparator.comparing(person -> person.getDay()));
        comparator = comparator.thenComparing(Comparator.comparing(person -> person.getName()));
        Collections.sort(bdList, comparator);

        for(Person p : bdList)
            System.out.println(p.toStringShow());
        System.out.println();
    }

    ///////////////////////////////////////////////////////////////////////////////
    //2 - поиск в списке
    private void find(){

        int isFind = 0;
        System.out.println("Введите имя для поиска:");
        name = sc.nextLine();
        if(name.length()>0) {
            for (Person p : bdList) {
                if (p.getName().contains(name)) {
                    System.out.println(p.toStringShow());
                    isFind++;
                }
            }
        }
        System.out.println("Нашлось "+isFind+" записей:");
        System.out.println();
    }

    //////////////////////////////////////////////////////////////////////////////
    //3 - добавить запись
    private void addPerson(){
            System.out.println("Как зовут именинника:");
            name = sc.nextLine();

            if(name.length()>0) {
                name = name.replaceAll(" ","");
                sb = new StringBuffer(name);
                sb.replace(0, name.length(), name.toLowerCase().trim());
                sb.replace(0, 1, name.substring(0, 1).toUpperCase());
                addMonth();
            }
            else
                addPerson();
    }
    private void addMonth(){
        System.out.println("Выберите месяц рождения (от 1 до 12):");
        try {
            month = Integer.parseInt(sc.nextLine());
            if (month >= 1 && month <= 12)
                addDay();
            else
                addMonth();
        }
        catch (NumberFormatException e) {
            //e.printStackTrace();
            System.out.println("ошибка ввода месяца");
            addMonth();
        }

    }
    private void addDay(){
        System.out.println("Укажите число (от 1 до 31):");
        try {
            day = Integer.parseInt(sc.nextLine());

            if(day<1 ||
                    ((month==1 || month==3 || month==5 || month==7 || month==8 || month==10 || month==12) && day>31) ||
                    ((month==4 || month==6 || month==9 || month==11) && day>30) ||
                    (month==2 && day>29) ){
                System.out.println("ошибка ввода дня");
                addDay();
            }
            else {
                bdList.add(new Person(sb.toString(), day, month));
                saveDatabase();
            }
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            System.out.println("ошибка ввода числа");
            addDay();
        }
    }

    ////////////////////////////////////////////////////////////////////////
    //4 - удалить запись
    private void delPerson(){
        boolean isFind = false;
        System.out.println("Введите имя для удаления:");
        name = sc.nextLine();
        int prove = 0;
        if(name.length()>0) {
            for (Person p : bdList) {
                if (p.getName().contains(name) || p.getName().contains(name.toLowerCase())) {
                    isFind = true;
                    System.out.println("Найдена запись:");
                    System.out.println(p.toStringShow());
                    System.out.println("Удалить?");
                    System.out.println("1 - удалить");
                    System.out.println("2 - отмена");
                    try {
                        prove = Integer.parseInt(sc.nextLine());
                        System.out.println(prove);
                        if (prove == 2)
                            break;
                        else if (prove == 1) {
                            bdList.remove(p);
                            System.out.println("Запись удалена");
                            saveDatabase();
                            break;
                        } else {
                            System.out.println("Ошибка меню удаления");
                            delPerson();
                        }
                    } catch (NumberFormatException e) {
                        //e.printStackTrace();
                        System.out.println("ошибка меню удаления");
                        delPerson();
                    }

                }
            }
        }
        if(!isFind)
            System.out.println("Записей не найдено");
    }

    ////////////////////////////////////////////////////////////////////////
    //5 - редактировать запись
    private void correctPerson(){
        boolean isFind=false;
        int prove = 0;
        System.out.println("Введите имя для редактирования:");
        name = sc.nextLine();
        if(name.length()>0) {
            for (Person p : bdList) {
                if (p.getName().contains(name)) {
                    isFind = true;
                    System.out.println(p.toStringShow());
                    System.out.println("Выполнить действие: ");
                    System.out.println("1 - редактировать эту запись");
                    System.out.println("2 - искать дальше");
                    System.out.println("3 - отмена");
                    try {
                        prove = Integer.parseInt(sc.nextLine());
                        System.out.println(prove);
                        if (prove == 3)
                            break;
                        else if(prove == 2)
                            continue;
                        else if (prove == 1) {
                            bdList.remove(p);
                            addPerson();
                            System.out.println("Запись отредактирована");
                            saveDatabase();
                            break;
                        } else {
                            System.out.println("Ошибка меню редактирования");
                            correctPerson();
                        }
                    } catch (NumberFormatException e) {
                        //e.printStackTrace();
                        System.out.println("ошибка меню редактирования");
                        delPerson();
                    }
                }
            }
        }
        if(!isFind)
            System.out.println("Записей не найдено");
        System.out.println();
    }

    ////////////////////////////////////////////////////////////////////////
    //сохранение в файл
    private void saveDatabase(){
        System.out.println("обновление базы данных...");
        System.out.println();
        try {
            FileWriter fWriter = new FileWriter(bdPath);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            for(Person p : bdList) {
                bWriter.write(p.toString()+"\n");
            }
            bWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////////////////////////////////////////////////////
    //чтение из файла
    private void readDatabase(){
        System.out.println("чтение базы данных...");

        list = new ArrayList<>();
        try {
            Stream<String> stream = Files.lines(Paths.get(bdPath), StandardCharsets.UTF_8);
            list = stream.collect(Collectors.toList());
            for(String s : list){
                String[] str = s.split(" ");
               bdList.add(new Person(str[0], Integer.parseInt(str[1]), Integer.parseInt(str[3])));
            }
            System.out.println("Всего записей: "+list.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
