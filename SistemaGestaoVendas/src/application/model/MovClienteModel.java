package application.model;

import java.time.LocalDateTime;

public class MovClienteModel {
    private int vendaId;
    private LocalDateTime data;
    private double total;
    private String status;

    public MovClienteModel(int vendaId, LocalDateTime data, double total, String status) {
        this.vendaId = vendaId;
        this.data = data;
        this.total = total;
        this.status = status;
    }

    public int getVendaId() {
        return vendaId;
    }

    public LocalDateTime getData() {
        return data;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }
}
