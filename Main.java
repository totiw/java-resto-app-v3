import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

// ABSTRACT MENU ITEM
abstract class MenuItem {
  String nama;
  String kategori;
  double harga;

  MenuItem(String nama, String kategori, double harga) {
    this.nama = nama;
    this.kategori = kategori;
    this.harga = harga;
  }

  public abstract void tampilMenu();

  // format rupiah
  static String toIDR(int nominal) {
    NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    formatter.setMaximumFractionDigits(0);
    formatter.setMinimumFractionDigits(0);
    return formatter.format(nominal);
  }
}

// SUB CLASS PESANAN
class Pesanan extends MenuItem {
  int quantity;

  Pesanan(String nama, String kategori, double harga, int quantity) {
    super(nama, kategori, harga);
    this.quantity = quantity;
  }

  @Override
  public void tampilMenu() {
    System.out
        .println(
            "- " + this.nama + " - " + toIDR((int) this.harga) + " (" + this.kategori + ")" + " x " + this.quantity);
  }

  int getSubtotal() {
    return (int) (this.harga * this.quantity);
  }
}

// SUB CLASS MAKANAN
class Makanan extends MenuItem {
  String jenisMakanan;

  Makanan(String nama, double harga, String jenisMakanan) {
    super(nama, "MAKANAN", harga);
    this.jenisMakanan = jenisMakanan;
  }

  // menampilkan menu
  @Override
  public void tampilMenu() {
    System.out
        .println(
            " (" + this.kategori + " - " + this.jenisMakanan + ") - " + this.nama + " - " + toIDR((int) this.harga));
  }
}

// SUB CLASS MINUMAN
class Minuman extends MenuItem {
  String jenisMinuman;

  Minuman(String nama, double harga, String jenisMinuman) {
    super(nama, "MINUMAN", harga);
    this.jenisMinuman = jenisMinuman;
  }

  @Override
  public void tampilMenu() {
    System.out
        .println(
            " (" + this.kategori + " - " + this.jenisMinuman + ") - " + this.nama + " - " + toIDR((int) this.harga));
  }
}

// SUB CLASS DISKON
class Diskon extends MenuItem {
  double diskon;

  Diskon(String nama, double diskon) {
    super(nama, "DISKON", 0);
    this.diskon = diskon;
  }

  @Override
  public void tampilMenu() {
    System.out.println(this.nama + " - " + this.diskon + "% (" + this.kategori + ")");
  }
}

public class Main {
  static Scanner input = new Scanner(System.in);
  static List<MenuItem> daftarMenu = new ArrayList<>();
  static List<Pesanan> pesanan = new ArrayList<>();

  // LOAD MENU FROM FILE

  static void loadMenuFromFile(String filename) {
    try {
      Scanner scannerFile = new Scanner(new File(filename));

      while (scannerFile.hasNextLine()) {
        String data = scannerFile.nextLine();

        String[] menu = data.split("\\|");

        String kategori = menu[0].trim();
        String nama = menu[1].trim();
        double harga = Double.parseDouble(menu[2].trim());

        switch (kategori) {
          case "MAKANAN":
            daftarMenu.add(new Makanan(nama, harga, menu[3].trim()));
            break;
          case "MINUMAN":
            daftarMenu.add(new Minuman(nama, harga, menu[3].trim()));
            break;
          case "DISKON":
            daftarMenu.add(new Diskon(nama, Double.parseDouble(menu[3].trim())));
            break;
        }
      }

      scannerFile.close();
    } catch (FileNotFoundException e) {
      System.out.println("File menu tidak ditemukan!");
    }
  }

  // -------- READ MENU --------

  static void getListMenu() {
    getListMenu(null);
  }

  static void getListMenu(String kategori) {
    System.out.println("\n=========   List    =========");
    System.out.println("=========   Menu    =========");

    if (kategori == null || kategori.equalsIgnoreCase("MAKANAN")) {
      System.out.println("\n--------- Makanan ---------\n");
      for (MenuItem item : daftarMenu) {
        if (item.kategori.equalsIgnoreCase("MAKANAN")) {
          item.tampilMenu();
        }
      }
    }

    if (kategori == null || kategori.equalsIgnoreCase("MINUMAN")) {
      System.out.println("\n--------- Minuman ---------\n");
      for (MenuItem item : daftarMenu) {
        if (item.kategori.equalsIgnoreCase("MINUMAN")) {
          item.tampilMenu();
        }
      }
    }

  }

  // -------- CREATE/ADD MENU --------

  static void addMenu() {
    while (true) {
      System.out.println("\n=========   Tambah Menu    =========");
      System.out.println("1. Makanan");
      System.out.println("2. Minuman");
      System.out.print("Pilih kategori menu: ");
      String kategori = input.nextLine().trim();

      if (!kategori.equals("1") && !kategori.equals("2")) {
        System.out.println("\nKategori tidak valid.");
        continue;
      }

      System.out.print("Masukkan nama menu: ");
      String nama = input.nextLine().trim();
      System.out.print("Masukkan harga menu: ");
      double harga = Double.parseDouble(input.nextLine().trim());

      if (kategori.equals("1")) {
        System.out.print("Masukkan jenis makanan: ");
        String jenisMakanan = input.nextLine().trim();
        daftarMenu.add(new Makanan(nama, harga, jenisMakanan));
      } else {
        System.out.print("Masukkan jenis minuman: ");
        String jenisMinuman = input.nextLine().trim();
        daftarMenu.add(new Minuman(nama, harga, jenisMinuman));
      }

      try (BufferedWriter writer = new BufferedWriter(new FileWriter("menu.txt"))) {
        for (int i = 0; i < daftarMenu.size(); i++) {
          MenuItem item = daftarMenu.get(i);
          if (item instanceof Makanan) {
            Makanan makanan = (Makanan) item;
            writer.write("MAKANAN|" + makanan.nama + "|" + makanan.harga + "|" + makanan.jenisMakanan);
          } else if (item instanceof Minuman) {
            Minuman minuman = (Minuman) item;
            writer.write("MINUMAN|" + minuman.nama + "|" + minuman.harga + "|" + minuman.jenisMinuman);
          } else if (item instanceof Diskon) {
            Diskon diskon = (Diskon) item;
            writer.write("DISKON|" + diskon.nama + "|" + diskon.harga + "|" + diskon.diskon);
          } else {
            continue;
          }

          if (i < daftarMenu.size() - 1) {
            writer.newLine();
          }

        }

        System.out.println("\nMenu berhasil ditambahkan.");
        return;

      } catch (IOException e) {
        System.out.println("\nGagal menambahkan menu.");
        return;
      }
    }

  }

  // -------- START ORDER --------

  static void startOrder() {
    pesanan.clear();
    System.out.println("\n=========   Pemesanan Pelanggan    =========");
    while (true) {
      getListMenu();
      System.out.print("\nMasukkan nama menu yang ingin dipesan (atau ketik 'selesai' untuk menyelesaikan): ");
      String namaMenu = input.nextLine().trim();

      if (namaMenu.equalsIgnoreCase("selesai")) {
        break;
      }

      MenuItem menuItem = null;
      for (MenuItem item : daftarMenu) {
        if (item.nama.equalsIgnoreCase(namaMenu)) {
          menuItem = item;
          break;
        }
      }

      if (menuItem == null) {
        System.out.println("Menu tidak ditemukan.");
        continue;
      }

      System.out.print("Masukkan jumlah pesanan: ");
      int quantity = Integer.parseInt(input.nextLine().trim());
      pesanan.add(new Pesanan(menuItem.nama, menuItem.kategori, menuItem.harga, quantity));
      System.out.println("Pesanan berhasil ditambahkan.");
    }

    // Menampilkan ringkasan pesanan
    if (!pesanan.isEmpty()) {
      printBill();
    } else {
      System.out.println("Tidak ada pesanan.");
    }
  }

  // -------- PRINT BILL --------

  static void printBill() {
    System.out.println("\n=========   Ringkasan Pesanan    =========");
    int total = 0;
    for (Pesanan p : pesanan) {
      p.tampilMenu();
      total += p.getSubtotal();
    }

    // Cek dan hitung diskon yang didapat
    double diskon = 0;
    LocalDate tanggal = LocalDate.now();
    DayOfWeek hari = tanggal.getDayOfWeek();

    // Diskon 15% untuk akhir pekan (Sabtu dan Minggu)
    // if (hari != DayOfWeek.SATURDAY && hari != DayOfWeek.SUNDAY) {
    if (hari == DayOfWeek.SATURDAY || hari == DayOfWeek.SUNDAY) {
      System.out.println("\nHari ini adalah akhir pekan. Diskon 15% berlaku!");
      diskon = diskon + 0.15;
    }

    // Diskon 10% untuk total pesanan lebih dari Rp100.000
    if (total > 100000) {
      System.out.println("\nTotal pesanan lebih dari Rp100.000. Diskon 10% berlaku!");
      diskon = diskon + 0.10;
    }

    System.out.println("\nSubTotal: " + MenuItem.toIDR(total));
    System.out.println("Diskon: " + MenuItem.toIDR((int) (total * diskon)));
    System.out.println("----------------------------------------------------------");
    System.out.println("Total: " + MenuItem.toIDR((int) (total - (total * diskon))));

    try (BufferedWriter writer = new BufferedWriter(new FileWriter("order_" + System.currentTimeMillis() + ".txt"))) {
      for (int i = 0; i < pesanan.size(); i++) {
        Pesanan p = pesanan.get(i);
        writer.write(p.nama + "|" + p.harga + "|" + p.quantity);
        if (i < pesanan.size() - 1) {
          writer.newLine();
        }

      }

      System.out.println("\nStruk berhasil disimpan.");
      return;

    } catch (IOException e) {
      System.out.println("\nGagal menyimpan struk.");
      return;
    }
  }

  // -------- MAIN MENU --------

  static void showMainMenu() {
    while (true) {
      System.out.println("\n======== Menu Utama ========");
      System.out.println("1. Pemesanan pelanggan");
      System.out.println("2. Tambah menu");
      System.out.println("0. Keluar");
      System.out.print("Pilih menu: ");

      String menu_option = input.nextLine().trim();
      switch (menu_option) {

        // 1. Pesanan Pelanggan
        case "1":
          startOrder();
          break;

        // 2. Menambahkan Menu
        case "2":
          addMenu();
          break;

        // 0. Keluar Aplikasi
        case "0":
          System.out.println("Terima kasih!");
          return;

        default:
          System.out.println("\nPilih menu berikut dengan benar.\n");
      }
    }
  }

  public static void main(String[] args) {
    loadMenuFromFile("menu.txt");
    showMainMenu();
  }
}
