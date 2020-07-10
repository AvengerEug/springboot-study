package com.eugene.sumarry.springbootredis.model;

public class Pagination {

    private Long pageIndex;
    private Long totalCount;
    private Long pageSize;
    private Long totalPage;

    private Long offset;

    public Long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    /**
     * 由当前查询条件查询总数和pageSize计算总页数
     * @param totalCount
     */
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        this.totalPage = (totalCount == 0 || totalCount < this.pageSize) ? 1 : (totalCount + pageSize - 1) / pageSize;

        if (totalCount <= pageSize) {
            this.pageIndex = 1L;
        }

        this.offset = (this.pageIndex - 1) * this.pageSize;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalPage() {
        return this.totalPage;
    }

    public Long getOffset() {
        return offset;
    }
}
