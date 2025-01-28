package sqlite_db;
/**
 * @author kumar
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Scanner; 

public class LibraryManagement {

    public static void main(String[] args) {
        String url = "jdbc:sqlite:C:/DB_Collection/sample.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connected to the database");

                // Create Tables for books, members, and transactions
                createTables(conn);

                // Create a scanner for user input
                Scanner scanner = new Scanner(System.in);
                boolean exit = false;

                // Menu-driven approach with switch-case
                while (!exit) {
                    System.out.println("Library Management System");
                    System.out.println("1. Add Book");
                    System.out.println("2. Add Member");
                    System.out.println("3. Borrow Book");
                    System.out.println("4. Query Books");
                    System.out.println("5. Query Transactions");
                    System.out.println("6. Exit");
                    System.out.print("Choose an option: ");

                    int choice = scanner.nextInt();
                    scanner.nextLine();  // Consume newline character

                    switch (choice) {
                        case 1:
                            System.out.print("Enter book title: ");
                            String title = scanner.nextLine();
                            System.out.print("Enter author name: ");
                            String author = scanner.nextLine();
                            System.out.print("Enter genre: ");
                            String genre = scanner.nextLine();
                            addBook(conn, title, author, genre);
                            break;

                        case 2:
                            System.out.print("Enter member name: ");
                            String name = scanner.nextLine();
                            System.out.print("Enter address: ");
                            String address = scanner.nextLine();
                            addMember(conn, name, address);
                            break;

                        case 3:
                            System.out.print("Enter book ID to borrow: ");
                            int bookId = scanner.nextInt();
                            System.out.print("Enter member ID: ");
                            int memberId = scanner.nextInt();
                            borrowBook(conn, bookId, memberId);
                            break;

                        case 4:
                            queryBooks(conn);
                            break;

                        case 5:
                            queryTransactions(conn);
                            break;

                        case 6:
                            exit = true;
                            System.out.println("Exiting the system...");
                            break;

                        default:
                            System.out.println("Invalid choice, please try again.");
                    }
                }
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws Exception {
        Statement stmt = conn.createStatement();

        // Create Books Table
        stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "genre TEXT)");

        // Create Members Table
        stmt.execute("CREATE TABLE IF NOT EXISTS members (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "address TEXT)");

        // Create Transactions Table (borrow/return records)
        stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id INTEGER PRIMARY KEY," +
                "book_id INTEGER," +
                "member_id INTEGER," +
                "transaction_type TEXT," +
                "FOREIGN KEY(book_id) REFERENCES books(id)," +
                "FOREIGN KEY(member_id) REFERENCES members(id))");
    }

    private static void addBook(Connection conn, String title, String author, String genre) throws Exception {
        String sql = "INSERT INTO books (title, author, genre) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, genre);
            pstmt.executeUpdate();
            System.out.println("Book added successfully.");
        }
    }

    private static void addMember(Connection conn, String name, String address) throws Exception {
        String sql = "INSERT INTO members (name, address) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.executeUpdate();
            System.out.println("Member added successfully.");
        }
    }

    private static void borrowBook(Connection conn, int bookId, int memberId) throws Exception {
        String sql = "INSERT INTO transactions (book_id, member_id, transaction_type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setString(3, "borrow");
            pstmt.executeUpdate();
            System.out.println("Book borrowed successfully.");
        }
    }

    private static void queryBooks(Connection conn) throws Exception {
        String sql = "SELECT * FROM books";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("Genre: " + rs.getString("genre"));
                System.out.println("-----------------------------");
            }
        }
    }

    private static void queryTransactions(Connection conn) throws Exception {
        String sql = "SELECT t.transaction_id, b.title AS book_title, m.name AS member_name, t.transaction_type " +
                "FROM transactions t " +
                "JOIN books b ON t.book_id = b.id " +
                "JOIN members m ON t.member_id = m.id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Transaction ID: " + rs.getInt("transaction_id"));
                System.out.println("Book: " + rs.getString("book_title"));
                System.out.println("Member: " + rs.getString("member_name"));
                System.out.println("Transaction Type: " + rs.getString("transaction_type"));
                System.out.println("-----------------------------");
            }
        }
    }
}
