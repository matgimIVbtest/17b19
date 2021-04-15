package rs.edu.matgim.zadatak;

import java.util.Scanner;

public class Program {

    public static void main(String[] args) {

        DB _db = new DB();
        _db.printUkupnaDuzina();
        
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Unesite ID puta koji se prekinuo >>> ");
        int IDPut = sc.nextInt();
        _db.zadatak(IDPut);
        
    }
}
