import{j as l,s as i,a8 as a}from"./index-Cg5IDLAj.js";function s(r){var n,o,e="";if(typeof r=="string"||typeof r=="number")e+=r;else if(typeof r=="object")if(Array.isArray(r))for(n=0;n<r.length;n++)r[n]&&(o=s(r[n]))&&(e&&(e+=" "),e+=o);else for(n in r)r[n]&&(e&&(e+=" "),e+=n);return e}function c(){for(var r=0,n,o,e="";r<arguments.length;)(n=arguments[r++])&&(o=s(n))&&(e&&(e+=" "),e+=o);return e}const p=i("span")(({theme:r})=>`
      display: inline-block;
      align-items: center;

      &.flexItem {
        display: inline-flex;
      }
      
      &.MuiText {

        &-black {
          color: ${r.palette.common.black}
        }

        &-primary {
          color: ${r.palette.primary.main}
        }
        
        &-secondary {
          color: ${r.palette.secondary.main}
        }
        
        &-success {
          color: ${r.palette.success.main}
        }
        
        &-warning {
          color: ${r.palette.warning.main}
        }
              
        &-error {
          color: ${r.palette.error.main}
        }
        
        &-info {
          color: ${r.palette.info.main}
        }
      }
`),f=({className:r,color:n="secondary",flex:o,children:e,...t})=>l.jsx(p,{className:c("MuiText",`MuiText-${n}`,{flexItem:o},r),...t,children:e});f.propTypes={children:a.node,className:a.string,color:a.oneOf(["primary","secondary","error","warning","success","info","black"])};export{f as T};
