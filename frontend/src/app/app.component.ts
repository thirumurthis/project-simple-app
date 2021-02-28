import { Component, OnInit } from '@angular/core';
import {HelperService} from './helper.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'simple-app';

  constructor (private helper: HelperService){
      }
  message : any = {};
  ngOnInit(){
     this.helper.getData().subscribe( data => {
       this.message = data;
       console.log("response: "+this.message.toString());
     });
  }
}
