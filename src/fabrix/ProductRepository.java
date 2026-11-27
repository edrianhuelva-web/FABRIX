package fabrix;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final String HEADER = "sku,name,category,collection,size,price,stock";
    private final Path file;

    public ProductRepository(Path file) { this.file = file; }

    public List<Product> loadAll() throws IOException {
        if (!HEADER.equals(CsvUtil.header(file))) saveAll(seed());
        List<Product> list = new ArrayList<>();
        for (String[] r : CsvUtil.read(file)) {
            list.add(new Product(r[0], r[1], r[2], r[3], r[4], Double.parseDouble(r[5]), Integer.parseInt(r[6])));
        }
        return list;
    }

    public void saveAll(List<Product> products) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Product p : products) {
            lines.add(CsvUtil.toLine(p.getSku(), p.getName(), p.getCategory(), p.getCollection(), p.getSize(),
                    String.valueOf(p.getPrice()), String.valueOf(p.getStock())));
        }
        CsvUtil.write(file, HEADER, lines);
    }

    private List<Product> seed() {
        List<Product> s = new ArrayList<>();
        s.add(new Product("FX-LS-001", "Linen Shirt", "Tops", "Linen / SS26", "M", 1290, 3));
        s.add(new Product("FX-TT-002", "Tailored Trousers", "Bottoms", "Tailoring", "S", 1890, 2));
        s.add(new Product("FX-SB-003", "Silk Blouse", "Tops", "Silk Edit", "M", 2190, 4));
        s.add(new Product("FX-KC-004", "Knit Cardigan", "Outerwear", "Knitwear", "L", 1790, 3));
        s.add(new Product("FX-HD-005", "Oversized Heavy Hoodie", "Outerwear", "Heavyweight", "L", 1490, 248));
        s.add(new Product("FX-TS-006", "Boxy Essential Tee", "Tops", "Essentials", "M", 590, 1240));
        s.add(new Product("FX-CG-007", "Pleated Cargo Pants", "Bottoms", "Utility / AW26", "L", 1390, 186));
        s.add(new Product("FX-JK-008", "Raw Denim Jacket", "Outerwear", "Raw Denim", "M", 2290, 0));
        s.add(new Product("FX-PL-009", "Classic Polo Shirt", "Tops", "Essentials", "S", 790, 95));
        s.add(new Product("FX-DR-010", "Floral Summer Dress", "Dresses", "Summer / SS26", "S", 1690, 15));
        s.add(new Product("FX-JN-011", "Slim Fit Jeans", "Bottoms", "Raw Denim", "M", 1590, 28));
        s.add(new Product("FX-LT-012", "Linen Trousers", "Bottoms", "Linen / SS26", "L", 1190, 12));
        return s;
    }
}
