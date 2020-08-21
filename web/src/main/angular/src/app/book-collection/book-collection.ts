import { Book } from '../book';
import { PageableList } from '../common';

export class BookCollection {
  id: number;
  name: string;
  parentBookCollection: BookCollection;
  bookCollections: PageableList<BookCollection>;
  numberOfBookCollections: number;
  books: PageableList<Book>;
  numberOfBooks: number;
}