package com.pillchill.migration.network.server.handlers;

import com.pillchill.migration.db.JPAUtil;
import com.pillchill.migration.entity.ChiTietHoaDon;
import com.pillchill.migration.entity.HoaDon;
import com.pillchill.migration.network.communication.Request;
import com.pillchill.migration.network.communication.Response;
import com.pillchill.migration.network.communication.command.HoaDonCM;
import com.pillchill.migration.network.server.CommandHandler;
import com.pillchill.migration.repository.IChiTietHoaDonRepository;
import com.pillchill.migration.repository.IHoaDonRepository;
import com.pillchill.migration.repository.impl.ChiTietHoaDonRepository;
import com.pillchill.migration.repository.impl.HoaDonRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class HoaDonCommandHandler implements CommandHandler {
    private final IHoaDonRepository hoaDonRepository;
    private final IChiTietHoaDonRepository chiTietHoaDonRepository;

    public HoaDonCommandHandler() {
        this.hoaDonRepository = new HoaDonRepository();
        this.chiTietHoaDonRepository = new ChiTietHoaDonRepository();
    }

    public HoaDonCommandHandler(IHoaDonRepository hoaDonRepository, IChiTietHoaDonRepository chiTietHoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
    }

    @Override
    public Response handle(Request request) {
        if (request.getSessionUserId() == null || request.getSessionUserId().isBlank()) {
            return Response.error("Bạn chưa đăng nhập");
        }

        if (request.getCommand() == null || !request.getCommand().startsWith("HOA_DON.")) {
            return Response.error("Command hóa đơn không hợp lệ");
        }

        String action = request.getCommand().substring("HOA_DON.".length());
        try {
            return switch (HoaDonCM.valueOf(action)) {
                case LIST_ALL, GET_5_FIELD_ALL -> {
                    List<HoaDon> result = findAllActiveHoaDon();
                    yield Response.success(result, "Lấy danh sách hóa đơn thành công");
                }
                case GET_BY_ID -> {
                    String maHoaDon = (String) request.getData();
                    Optional<HoaDon> result = hoaDonRepository.findById(maHoaDon);
                    yield Response.success(result.orElse(null), result.isPresent() ? "Lấy hóa đơn thành công" : "Không tìm thấy hóa đơn");
                }
                case GET_5_FIELD_BY_THUOC -> {
                    String maThuoc = (String) request.getData();
                    List<HoaDon> result = findHoaDonByThuoc(maThuoc);
                    yield Response.success(result, "Lấy danh sách hóa đơn theo thuốc thành công");
                }
                case GET_BY_THANG_NAM -> {
                    int[] values = extractMonthYear(request);
                    List<HoaDon> result = findHoaDonByThangNam(values[0], values[1]);
                    yield Response.success(result, "Lấy danh sách hóa đơn theo tháng/năm thành công");
                }
                case GET_CHI_TIET_BY_MA_HOA_DON -> {
                    String maHoaDon = (String) request.getData();
                    List<ChiTietHoaDon> result = chiTietHoaDonRepository.findByMaHoaDon(maHoaDon);
                    yield Response.success(result, "Lấy chi tiết hóa đơn thành công");
                }
                case GET_NAM_CO_HOA_DON -> {
                    List<Integer> result = findNamCoHoaDon();
                    yield Response.success(result, "Lấy danh sách năm có hóa đơn thành công");
                }
                case GET_THANG_CO_HOA_DON_TRONG_NAM -> {
                    int nam = (int) request.getData();
                    List<Integer> result = findThangCoHoaDonTrongNam(nam);
                    yield Response.success(result, "Lấy danh sách tháng có hóa đơn thành công");
                }
            };
        } catch (IllegalArgumentException e) {
            return Response.error("Command hóa đơn không hỗ trợ: " + action);
        } catch (Exception e) {
            return Response.error("Không thể xử lý hóa đơn: " + e.getMessage());
        }
    }

    private List<HoaDon> findAllActiveHoaDon() {
        EntityManager entityManager = JPAUtil.getEntityManager();
        try {
            return entityManager.createQuery(
                            "select h from HoaDon h where h.isActive = true order by h.ngayBan desc, h.maHoaDon desc",
                            HoaDon.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    private List<HoaDon> findHoaDonByThuoc(String maThuoc) {
        EntityManager entityManager = JPAUtil.getEntityManager();
        try {
            return entityManager.createQuery(
                            "select distinct h from HoaDon h join ChiTietHoaDon c on c.id.maHoaDon = h.maHoaDon " +
                                    "where h.isActive = true and c.isActive = true and c.id.maThuoc = :maThuoc " +
                                    "order by h.ngayBan desc, h.maHoaDon desc",
                            HoaDon.class)
                    .setParameter("maThuoc", maThuoc)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    private List<HoaDon> findHoaDonByThangNam(int thang, int nam) {
        EntityManager entityManager = JPAUtil.getEntityManager();
        try {
            return entityManager.createQuery(
                            "select h from HoaDon h " +
                                    "where h.isActive = true and month(h.ngayBan) = :thang and year(h.ngayBan) = :nam " +
                                    "order by h.ngayBan desc, h.maHoaDon desc",
                            HoaDon.class)
                    .setParameter("thang", thang)
                    .setParameter("nam", nam)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    private List<Integer> findNamCoHoaDon() {
        EntityManager entityManager = JPAUtil.getEntityManager();
        try {
            return entityManager.createQuery(
                            "select distinct year(h.ngayBan) from HoaDon h where h.isActive = true order by year(h.ngayBan)",
                            Integer.class)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    private List<Integer> findThangCoHoaDonTrongNam(int nam) {
        EntityManager entityManager = JPAUtil.getEntityManager();
        try {
            return entityManager.createQuery(
                            "select distinct month(h.ngayBan) from HoaDon h " +
                                    "where h.isActive = true and year(h.ngayBan) = :nam order by month(h.ngayBan)",
                            Integer.class)
                    .setParameter("nam", nam)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    private int[] extractMonthYear(Request request) {
        Object data = request.getData();
        if (!(data instanceof int[] values) || values.length < 2) {
            throw new IllegalArgumentException("Dữ liệu tháng/năm không hợp lệ");
        }
        return values;
    }
}
