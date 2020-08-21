import { BookCollection } from '../book-collection';
import { BookMark } from '../book-mark';

export class Book {
  id: number;
  name: string;
  numberOfPages: number;
  bookCollection: BookCollection;
  bookMark: BookMark;
}