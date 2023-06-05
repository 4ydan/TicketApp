export class Merchandise {
  id: number;
  name: string;
  price: number;
  inStock: number;
  picture: string;
  costsPoints: number;
}

export class MerchandisePurchase {
  id: number;
  amount: number;
}

export class MerchandiseRedeem {
  userId: number;
  itemId: number;
  amount: number;
}
