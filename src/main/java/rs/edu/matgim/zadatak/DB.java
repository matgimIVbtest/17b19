package rs.edu.matgim.zadatak;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DB {

    String connectionString = "jdbc:sqlite:src\\main\\java\\KompanijaZaPrevoz.db";

    public void printUkupnaDuzina() {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT V.IDZap, Z.ImePrezime, SUM(Duzina)\n" +
            "FROM Zaposlen Z, Vozac V, Putovanje P, Vozi A\n" +
            "WHERE V.IDZap = Z.IDZap AND P.IDPut = A.IDPut AND A.IDZap = V.IDZap AND P.Status = 'G'\n" +
            "GROUP BY V.IDZap");
            while (rs.next()) {
                int IDZap = rs.getInt(1);
                String ImePrezime = rs.getString(2);
                int duzina = rs.getInt(3);

                System.out.println(String.format("%d\t%s\t%d", IDZap, ImePrezime, duzina));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    public int zadatak(int IdPut)
    {
        int broj = 0;
        //Kada se kamion pokvari, putovanje dobija status P
        String sql = "UPDATE Putovanje SET Status = 'P' WHERE IDPut = "+IdPut;
        String sql1 = "SELECT IDZap FROM Mehanicar";
        String sql2 = "SELECT IDZap FROM Popravlja";
        String sql3 = "SELECT IDKam FROM Putovanje WHERE IDPut = "+IdPut;
        String sql4 = "DELETE FROM SePrevozi WHERE IdPut = " + IdPut;
      
        try (Connection conn = DriverManager.getConnection(connectionString); 
             PreparedStatement ps = conn.prepareStatement(sql);
             PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2);
             PreparedStatement ps3 = conn.prepareStatement(sql3);
             PreparedStatement ps4 = conn.prepareStatement(sql4);
             ) {
            conn.setAutoCommit(false);
            List<Integer> IDMehanicari = new LinkedList<>();
            List<Integer> IDZauzeti = new LinkedList<>();
            
            ps.executeUpdate();
            
            ResultSet rs1 = ps1.executeQuery();
            while(rs1.next())
            {
                IDMehanicari.add(rs1.getInt(1));
            }
            
            ResultSet rs2 = ps2.executeQuery();
            while(rs2.next())
            {
                IDZauzeti.add(rs2.getInt(1));
            }
            IDMehanicari.removeAll(IDZauzeti); // Sada u listi IDMehanicari ostaju slobodni
            broj=IDMehanicari.size(); //broj mehanicara angazovanih za najnoviji kvar
            
            ResultSet rs3 = ps3.executeQuery();
            rs3.next();
            int idKam = rs3.getInt(1);
            for(int i=0;i<broj;i++)
            {
                String sqlUbaciUPopravlja = "INSERT INTO Popravlja(Dana,IDZap,IDKam) VALUES(1,?,?)";
                try (PreparedStatement psPom = conn.prepareStatement(sqlUbaciUPopravlja)) {

                    psPom.setInt(1, IDMehanicari.get(i));
                    psPom.setInt(2, idKam);
                    psPom.execute();
                    

                } catch (SQLException ex) {
                    System.out.println("Greska prilikom povezivanja na bazu");
                    System.out.println(ex);
                }
            }
            
            
            ps4.execute();
       
            conn.commit();
            System.out.println("Uspesna realizacija");

        } catch (SQLException ex) {
            System.out.println("Dogodila se greska.");
            System.out.println(ex);
        }
        
        
        
        
        return broj;
    }


}
