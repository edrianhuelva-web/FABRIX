package fabrix;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private static final String HEADER = "orderId,supplier,placedAt,status,lines";
    private final Path file;

    public OrderRepository(Path file) { this.file = file; }

    public List<Order> loadAll() throws IOException {
        if (!HEADER.equals(CsvUtil.header(file))) saveAll(seed());
        List<Order> orders = new ArrayList<>();
        for (String[] r : CsvUtil.read(file)) {
            List<CartItem> items = new ArrayList<>();
            for (String part : r[4].split("\\|")) {
                if (part.isBlank()) continue;
                String[] f = part.split("~");
                items.add(new CartItem(f[0], f[1], f[2], Integer.parseInt(f[3]), Double.parseDouble(f[4])));
            }
            orders.add(new Order(r[0], r[1], LocalDateTime.parse(r[2]), OrderStatus.valueOf(r[3]), items));
        }
        return orders;
    }

    public void saveAll(List<Order> orders) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Order o : orders) {
            StringBuilder sb = new StringBuilder();
            for (CartItem i : o.getItems()) {
                if (sb.length() > 0) sb.append('|');
                sb.append(clean(i.getSku())).append('~').append(clean(i.getName())).append('~')
                  .append(clean(i.getSize())).append('~').append(i.getQuantity()).append('~')
                  .append(i.getUnitPrice());
            }
            lines.add(CsvUtil.toLine(o.getId(), o.getSupplier(), o.getPlacedAt().withNano(0).toString(),
                    o.getStatus().name(), sb.toString()));
        }
        CsvUtil.write(file, HEADER, lines);
    }

    private static String clean(String s) { return s.replace("|", "/").replace("~", "-"); }

    private List<Order> seed() {
        List<Order> s = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        List<CartItem> a = new ArrayList<>();
        a.add(new CartItem("FX-JN-011", "Slim Fit Jeans", "M", 40, 1590));
        s.add(new Order("FRX-1045", "Maison Textiles", now.minusDays(9), OrderStatus.DELIVERED, a));

        List<CartItem> b = new ArrayList<>();
        b.add(new CartItem("FX-LT-012", "Linen Trousers", "L", 50, 1190));
        s.add(new Order("FRX-1046", "Studio Linen", now.minusDays(6), OrderStatus.SHIPPED, b));

        List<CartItem> c = new ArrayList<>();
        c.add(new CartItem("FX-SB-003", "Silk Blouse", "M", 30, 2190));
        c.add(new CartItem("FX-KC-004", "Knit Cardigan", "L", 25, 1790));
        s.add(new Order("FRX-1047", "Atelier Supply", now.minusDays(3), OrderStatus.PROCESSING, c));

        List<CartItem> d = new ArrayList<>();
        d.add(new CartItem("FX-LS-001", "Linen Shirt", "M", 60, 1290));
        d.add(new CartItem("FX-TT-002", "Tailored Trousers", "S", 40, 1890));
        s.add(new Order("FRX-1048", "Maison Textiles", now.minusDays(1), OrderStatus.PENDING, d));
        return s;
    }
}
