export class PageableList<T> {
    elements: T[];
    numberOfElements: number;
    page: number;
    pageSize: number;
    firstPage: number;
    lastPage: number;
    previousPage: number;
    nextPage: number;
  }