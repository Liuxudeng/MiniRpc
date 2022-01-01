public class test01 {

    public static void main(String[] args) {
         Person child = new Person();

        System.out.println(child.getClass());

        System.out.println("-----");
        System.out.println(child.getClass().getName());
        System.out.println("----------");
        System.out.println(child.getClass().getCanonicalName());
        System.out.println("--------");
        System.out.println(Person.class.getName());
        System.out.println("---------");
        System.out.println(Person.class.getCanonicalName());

    }

}
