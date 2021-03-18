import { BookCollection } from '../book-collection/book-collection';

export class User {
    id: number;
    name: string;
    password: string;
    roles: string[];
    rootBookCollection: BookCollection;
  }